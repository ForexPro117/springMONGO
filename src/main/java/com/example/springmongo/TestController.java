package com.example.springmongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private CustomMongoRepository customMongoRepository;

    @PostMapping("/")
    public List test(@RequestBody List<UUID> uuids) {
//        UUID uuid = UUID.randomUUID();
//        GeometryData data = new GeometryData();
//        data.setUuid(uuid);
//        customMongoRepository.save(data);
//        customMongoRepository.findById(uuid);
        var time = System.currentTimeMillis();
        var list = customMongoRepository.findAll(uuids);
        log.info(System.currentTimeMillis() - time + " ms");
        return list;
    }

    @GetMapping("/{batchSize}")
    public String getGeometryBatch(@PathVariable int batchSize) {
        log.info("Dropping collection...");
        mongoTemplate.dropCollection("fs.files");
        mongoTemplate.dropCollection("fs.chunks");
        log.info("Dropped!");

        var size = pgeometryRepository.countAllByCreateDateBefore(new Timestamp(new Date().getTime()));
        var dateToConvert = new Timestamp(new Date().getTime());

        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / (double)batchSize);
        for (int i = 0; i < chunks; i++) {
            Instant start = Instant.now();

            var page = pgeometryRepository.findAllByCreateDateBefore(dateToConvert, PageRequest.of(i, batchSize)).toList();

            log.info("Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");
            customMongoRepository.saveAll(page);
            log.info("Bulk insert of " + batchSize + " documents completed in " + Duration.between(start, Instant.now()).toMillis() + " milliseconds");
            log.info("STEP: " + (i + 1) + "/" + chunks);

        }

        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
        log.info("Date to find converted rows : " + dateToConvert);
        log.info("Table row count under time: " + size);

        return "ok " + (System.currentTimeMillis() - time) + " ms";

    }

}
