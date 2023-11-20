package com.example.springmongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@CrossOrigin
public class ConvertController {

    private static final Logger log = LoggerFactory.getLogger(ConvertController.class);

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    private CassandraTemplate template;

    @Autowired
    private CassandraGeometryDataRepository cassandraGeometryDataRepository;

    @GetMapping("/{batchSize}")
    public String getGeometryBatch(@PathVariable int batchSize) {
        log.info("Create keyspace...");
        template.getCqlOperations().execute(createKeyspace());
        log.info("Create table...");
        template.getCqlOperations().execute(createTable());
        log.info("Truncate table...");
        template.truncate(CassandraGeometryData.class);
        log.info("Ready to work!");



        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
        var dateToConvert = new Timestamp(new Date().getTime());
        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / (double) batchSize);


        for (int i = 0; i < chunks; i++) {
            Instant start = Instant.now();
            var list = pgeometryRepository.findAllByCreateDateBefore(dateToConvert, PageRequest.of(i, batchSize))
                    .map(this::convert).toList();

            log.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
            cassandraGeometryDataRepository.saveAll(list);
            log.info("Insert of " + batchSize + " rows completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
            log.info("STEP: " + (i + 1) + "/" + chunks);
        }


        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
        log.info("Date to find converted rows : " + dateToConvert);
        log.info("Table row count under time: " + size);
        return "ok " + (System.currentTimeMillis() - time) + " ms";

    }

    private CassandraGeometryData convert(GeometryData data) {
        CassandraGeometryData bin = new CassandraGeometryData();
        bin.setUuid(data.getUuid());
        bin.setIndices(getByteArray(data.getIndices()));
        bin.setVertices(getByteArray(data.getVertices()));
        bin.setNormals(getByteArray(data.getNormals()));
        bin.setColorsQuantized(getByteArray(data.getColorsQuantized()));
        bin.setColors(getByteArray(data.getColors()));
        bin.setHashCode(data.getHashCode());

        return bin;
    }

    private ByteBuffer getByteArray(double[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 8);
        for (double d : arr) {
            bb.putDouble(d);
        }
        return ByteBuffer.wrap(bb.array());
    }

    private ByteBuffer getByteArray(float[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (float d : arr) {
            bb.putFloat(d);
        }
        return ByteBuffer.wrap(bb.array());
    }

    private ByteBuffer getByteArray(int[] arr) {
        ByteBuffer bb = ByteBuffer.allocate(arr.length * 4);
        for (int d : arr) {
            bb.putInt(d);
        }
        return ByteBuffer.wrap(bb.array());
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
