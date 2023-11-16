package com.example.springmongo;

import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;

@RestController
@CrossOrigin
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private GeometryDataRepository geometryDataRepository;

    @Autowired
    private GeometryDataMongoRepository geometryDataMongoRepository;

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/insert")
    public ResponseEntity<?> insertData(@RequestBody MongoGeometryData data) {
        return ResponseEntity.ok(geometryDataMongoRepository.save(data));
    }

    @GetMapping("/get/{uuid}")
    public ResponseEntity<?> getdData(@PathVariable UUID uuid) {
        return ResponseEntity.ok(geometryDataMongoRepository.findById(uuid).orElse(null));
    }

    @PostMapping("/get/uuids")
    public ResponseEntity<?> getDatas(@RequestBody List<UUID> uuids) {
        long time = System.currentTimeMillis();
        Iterable<MongoGeometryData> list = geometryDataMongoRepository.findAllById(uuids);
        System.err.println("Get 150 gData: " + (System.currentTimeMillis() - time) + " мс");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/")
    public String getGeometryBatch() {
        LOG.info("Dropping collection...");
        mongoTemplate.dropCollection(MongoGeometryData.class);
        LOG.info("Dropped!");
        mongoTemplate.setWriteConcern(WriteConcern.W1.withJournal(true));


        var time = System.currentTimeMillis();
        var size = pgeometryRepository.count();
        int chunks = (int) Math.ceil(size / 500d);
        for (int i = 0; i < chunks; i++) {
            Instant start = Instant.now();

            BulkOperations bulkInsertion = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MongoGeometryData.class);
            var page = pgeometryRepository.findAll(PageRequest.of(i, 500));
            page.stream().forEach(el -> bulkInsertion.insert(modelMapper.map(el, MongoGeometryData.class)));

            LOG.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
            BulkWriteResult bulkWriteResult = bulkInsertion.execute();
            LOG.info("Bulk insert of " + bulkWriteResult.getInsertedCount() + " documents completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
            LOG.info("STEP: " + (i + 1) + "/" + chunks);

        }
        LOG.info("ok " + (System.currentTimeMillis() - time) + " ms");
        return "ok " + (System.currentTimeMillis() - time) + " ms";

    }

    private MongoGeometryDataBin convert(GeometryData data) {
        MongoGeometryDataBin bin = new MongoGeometryDataBin();
        bin.setUuid(data.getUuid());
        bin.setIndices(getByteArray(data.getIndices()));
        bin.setVertices(getByteArray(data.getVertices()));
        bin.setNormals(getByteArray(data.getNormals()));
        bin.setColorsQuantized(getByteArray(data.getColorsQuantized()));
        bin.setColors(getByteArray(data.getColors()));
        bin.setHashCode(data.getHashCode());

        return bin;
    }

    private byte[] getByteArray(double[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
        for (double d : arr) {
            bb.putDouble(d);
        }
        return bb.array();
    }

    private byte[] getByteArray(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (float d : arr) {
            bb.putFloat(d);
        }
        return bb.array();
    }

    private byte[] getByteArray(int[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (int d : arr) {
            bb.putInt(d);
        }
        return bb.array();
    }

}
