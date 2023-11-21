package com.example.springmongo;

import com.example.springmongo.repository.CassandraGDCustomRepo;
import com.example.springmongo.repository.CassandraGeometryDataRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;

@RestController
@CrossOrigin
public class ConvertController {

    private static final Logger log = LoggerFactory.getLogger(ConvertController.class);

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    private CassandraTemplate template;

    @Autowired
    private CassandraGDCustomRepo customRepo;

    @Autowired
    private CassandraGeometryDataRepository cassandraGeometryDataRepository;

    private static final String CSV_SEPARATOR = "\t";


  /*  @GetMapping("/")
    public String getGeometryBatch() {
        log.info("Dropping collection...");
        mongoTemplate.dropCollection(CassandraGeometryData.class);
        log.info("Dropped!");
        mongoTemplate.setWriteConcern(WriteConcern.W1.withJournal(true));

        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
        var dateToConvert = new Timestamp(new Date().getTime());

        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / 500d);
        for (int i = 0; i < chunks; i++) {
            Instant start = Instant.now();

            BulkOperations bulkInsertion = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, CassandraGeometryData.class);
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

    }*/

    //    @GetMapping("/")
//    public String getGeometryBatch() throws IOException {
//        log.info("Create keyspace...");
//        template.getCqlOperations().execute(createKeyspace());
//        log.info("Create table...");
//        template.getCqlOperations().execute(createTable());
//        log.info("Truncate table...");
//        template.truncate(CassandraGeometryData.class);
//        log.info("Ready to work!");
//
//
//        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
//        var dateToConvert = new Timestamp(new Date().getTime());
//        var time = System.currentTimeMillis();
//        int chunks = (int) Math.ceil(size / 100d);
//
//
//        for (int i = 0; i < chunks; i++) {
//            Instant start = Instant.now();
//            var list = pgeometryRepository.findAllByCreateDateBefore(dateToConvert, PageRequest.of(i, 100)).toList();
//            StringBuilder builder = new StringBuilder();
//            for (GeometryData element : list) {
//                builder.append(element.getUuid()).append(CSV_SEPARATOR);
//                builder.append(element.getHashCode()).append(CSV_SEPARATOR);
//                builder.append(Arrays.toString(getByteArray(element.getIndices()))).append(CSV_SEPARATOR);
//                builder.append(Arrays.toString(getByteArray(element.getVertices()))).append(CSV_SEPARATOR);
//                builder.append(Arrays.toString(getByteArray(element.getNormals()))).append(CSV_SEPARATOR);
//                builder.append(Arrays.toString(getByteArray(element.getColorsQuantized()))).append(CSV_SEPARATOR);
//                builder.append(Arrays.toString(getByteArray(element.getColors()))).append(CSV_SEPARATOR);
//                builder.append("\n");
//            }
//            log.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
//            try (InputStream tmpInputStream = new ByteArrayInputStream(builder.toString().getBytes())) {
//                template.getCqlOperations().execute("COPY pirsbim.geometry_data (uuid, hashCode, indices, vertices, normals, colorsQuantized, colors) from STDIN with NULL AS 'null' delimiter E'\t' csv",tmpInputStream);
//
//            }
//            log.info("Bulk insert of " + 100 + " documents completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
//            log.info("STEP: " + (i + 1) + "/" + chunks);
//        }
//
//
//        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
//        log.info("Date to find converted rows : " + dateToConvert);
//        log.info("Table row count under time: " + size);
//        return "ok " + (System.currentTimeMillis() - time) + " ms";
//
//    }
//    @GetMapping("/")
//    public String getGeometryBatch(){
//        log.info("Create keyspace...");
//        template.getCqlOperations().execute(createKeyspace());
//        log.info("Create table...");
//        template.getCqlOperations().execute(createTable());
//        log.info("Truncate table...");
//        template.truncate(CassandraGeometryData.class);
//        log.info("Ready to work!");
//        int batchSize = 250;
//
//
//        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
//        var dateToConvert = new Timestamp(new Date().getTime());
//        var time = System.currentTimeMillis();
//        int chunks = (int) Math.ceil(size / (double)batchSize);
//
//
//        for (int i = 0; i < chunks; i++) {
//            Instant start = Instant.now();
//            var list = pgeometryRepository.findAllByCreateDateBefore(dateToConvert, PageRequest.of(i, batchSize))
//                    .map(this::convert).toList();
//
//            log.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
//            cassandraGeometryDataRepository.saveAll(list);
//            log.info("Insert of " + batchSize + " rows completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
//            log.info("STEP: " + (i + 1) + "/" + chunks);
//        }
//
//
//        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
//        log.info("Date to find converted rows : " + dateToConvert);
//        log.info("Table row count under time: " + size);
//        return "ok " + (System.currentTimeMillis() - time) + " ms";
//
//    }

    @PostMapping("/insert")
    public ResponseEntity<?> set(@RequestBody CassandraGeometryData data) {
        long time = System.currentTimeMillis();
        data = customRepo.save(data);
        System.err.println(System.currentTimeMillis() - time);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/get/{uuid}")
    public ResponseEntity<?> get(@PathVariable UUID uuid) {
//        CassandraGeometryData data = cassandraGeometryDataRepository.findById(uuid).orElse(null);
        long time = System.currentTimeMillis();
        CassandraGeometryData data = customRepo.findById(uuid);
        System.err.println(System.currentTimeMillis() - time);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/get/uuids")
    public ResponseEntity<?> getUuids(@RequestBody List<UUID> uuid) {
        Set<CassandraGeometryData> data = customRepo.findAllById(uuid);
        data.forEach(el -> el.setUuid(null));
        long time = System.currentTimeMillis();
        List<CassandraGeometryData> result = customRepo.saveAll(data);
        System.err.println(System.currentTimeMillis() - time);
        return ResponseEntity.ok(result);
    }

//    @PostMapping("/get")
//    public ResponseEntity<?> get(@RequestBody List<UUID> uuidList) {
//        long time = System.currentTimeMillis();
//        List<CassandraGeometryData> data = cassandraGeometryDataRepository.findAllById(uuidList);
//        List<GeometryData> list = new ArrayList<>();
//        data.forEach(el -> {
//            GeometryData gData = new GeometryData();
//            gData.setUuid(el.getUuid());
//            gData.setHashCode(el.getHashCode());
//            gData.setVertices(getDoubleArray(el.getVertices()));
//            gData.setNormals(getFloatArray(el.getNormals()));
//            gData.setColors(getFloatArray(el.getColors()));
//            gData.setIndices(getIntArray(el.getIndices()));
//            gData.setColorsQuantized(getIntArray(el.getColorsQuantized()));
//            list.add(gData);
//        });
//        System.err.println("150 elements: " + (System.currentTimeMillis() - time));
//        return ResponseEntity.ok(list);
//    }

//    private CassandraGeometryData convert(GeometryData data) {
//        CassandraGeometryData bin = new CassandraGeometryData();
//        bin.setUuid(data.getUuid());
//        bin.setIndices(getByteArray(data.getIndices()));
//        bin.setVertices(getByteArray(data.getVertices()));
//        bin.setNormals(getByteArray(data.getNormals()));
//        bin.setColorsQuantized(getByteArray(data.getColorsQuantized()));
//        bin.setColors(getByteArray(data.getColors()));
//        bin.setHashCode(data.getHashCode());
//
//        return bin;
//    }

    private ByteBuffer getByteArray(double[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
        for (double d : arr) {
            bb.putDouble(d);
        }
        return bb;
    }

    private ByteBuffer getByteArray(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (float d : arr) {
            bb.putFloat(d);
        }
        return bb;
    }

    private ByteBuffer getByteArray(int[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (int d : arr) {
            bb.putInt(d);
        }
        return bb;
    }

    private int[] getIntArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        int[] ints = new int[array.length / 4];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = bb.getInt();
        }
        return ints;
    }

    private float[] getFloatArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        float[] floats = new float[array.length / 4];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = bb.getFloat();
        }
        return floats;
    }

    private double[] getDoubleArray(ByteBuffer arr) {
        var array = arr.array();
        ByteBuffer bb = ByteBuffer.wrap(array);
        double[] doubles = new double[array.length / 8];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = bb.getDouble();
        }
        return doubles;
    }

    private String createTable() {
        return "CREATE TABLE IF NOT EXISTS pirsbim.geometry_data (\n" +
                "uuid uuid PRIMARY KEY,\n" +
                "hash_code text,\n" +
                "indices blob,\n" +
                "vertices blob,\n" +
                "normals blob,\n" +
                "colors_quantized blob,\n" +
                "colors blob\n" +
                ");";
    }

    private String createKeyspace() {
        return "CREATE KEYSPACE IF NOT EXISTS pirsbim WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 }";
    }

}
