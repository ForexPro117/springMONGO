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

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private PGeometryDataRepository pgeometryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomMongoRepository<OnSaveData> customMongoRepository;

    @PostMapping("/")
    public List test(@RequestBody List<UUID> uuids) {
//        UUID uuid = UUID.randomUUID();
//        GeometryData data = new GeometryData();
//        data.setUuid(uuid);
//        customMongoRepository.save(data);
//        customMongoRepository.findById(uuid);
        var time = System.currentTimeMillis();
        customMongoRepository.deleteAll(uuids);
        log.info(System.currentTimeMillis() - time + " ms");
        return null;
    }

    @GetMapping("/drop")
    public void dropCollection() {
        log.info("Dropping collection...");
        mongoTemplate.dropCollection("fs.files");
        mongoTemplate.dropCollection("fs.chunks");
        log.info("Dropped!");
    }

    @GetMapping("/{batchSize}")
    public String batchConvertDB(@PathVariable int batchSize) {
        var dateToConvert = new Timestamp(new Date().getTime());
        var size = pgeometryRepository.countAllByCreateDateBeforeAndConvertedFalse(dateToConvert);
        log.info("Date to find converted rows : " + dateToConvert);


        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / (double) batchSize);
        for (int i = 0; i < chunks; i++) {
            pullBatch(batchSize, dateToConvert, i, chunks);
        }

        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
        log.info("Date to find converted rows : " + dateToConvert);
        log.info("Table row count under time: " + size);

        return "ok " + (System.currentTimeMillis() - time) + " ms \nDate to find converted rows : " + dateToConvert;

    }

    public void pullBatch(int batchSize, Timestamp dateToConvert, int i, int chunks) {
        try {
            log.info("STEP: " + (i + 1) + "/" + chunks);
            Instant start = Instant.now();
            var page = pgeometryRepository.findAllByCreateDateBeforeAndConvertedFalse(dateToConvert, PageRequest.of(i, batchSize))
                    .stream().map(GeometryData::convert).collect(Collectors.toList());

            log.info("1) Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms");

            customMongoRepository.saveAll(page);

            log.info("2) Saved in mongodb " + Duration.between(start, start = Instant.now()).toMillis() + " ms");

            pgeometryRepository.markAsConverted(page.parallelStream().map(OnSaveData::getUuid).collect(Collectors.toList()));

            log.info("3) Patch postgres " + Duration.between(start, Instant.now()).toMillis() + " ms");

        } catch (Exception ex) {
            log.error("STEP: " + i + "/" + chunks + " error:" + ex.getMessage());
            log.error("Date to find converted rows : " + dateToConvert);
            throw new RuntimeException();
        }
    }

}
