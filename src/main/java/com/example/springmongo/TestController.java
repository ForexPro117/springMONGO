package com.example.springmongo;

import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@CrossOrigin
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("/")
    public String getGeometryBatch() {
        log.info("Dropping collection...");
        mongoTemplate.dropCollection(MongoGeometryDataBin.class);
        log.info("Dropped!");
        mongoTemplate.setWriteConcern(WriteConcern.W1.withJournal(true));

        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
        var dateToConvert = new Timestamp(new Date().getTime());

        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / 500d);
        for (int i = 0; i < chunks; i++) {
            Instant start = Instant.now();

            BulkOperations bulkInsertion = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MongoGeometryDataBin.class);
            var page = pgeometryRepository.findAllByCreateDateBefore(dateToConvert, PageRequest.of(i, 500));
            page.stream().forEach(el -> bulkInsertion.insert(convert(el)));

            log.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
            BulkWriteResult bulkWriteResult = bulkInsertion.execute();
            log.info("Bulk insert of " + bulkWriteResult.getInsertedCount() + " documents completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
            log.info("STEP: " + (i + 1) + "/" + chunks);

        }

        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
        log.info("Date to find converted rows : " + dateToConvert);
        log.info("Table row count under time: " + size);

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

    private int[] getIntArray(byte[] arr) {
        ByteBuffer bb = ByteBuffer.wrap(arr);
        int[] ints = new int[arr.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = bb.getInt();
        }
        return ints;
    }

    private float[] getFloatArray(byte[] arr) {
        ByteBuffer bb = ByteBuffer.wrap(arr);
        float[] floats = new float[arr.length / 4];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = bb.getFloat();
        }
        return floats;
    }

    private double[] getDoubleArray(byte[] arr) {
        ByteBuffer bb = ByteBuffer.wrap(arr);
        double[] doubles = new double[arr.length / 8];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = bb.getDouble();
        }
        return doubles;
    }

}
