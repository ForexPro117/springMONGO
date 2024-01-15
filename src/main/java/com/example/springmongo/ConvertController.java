package com.example.springmongo;

import com.example.springmongo.geometrydata.CustomMongoRepository;
import com.example.springmongo.geometrydata.GeometryData;
import com.example.springmongo.geometrydata.GeometryDataRepository;
import com.example.springmongo.geometrydata.OnSaveData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ConvertController {

    private static final Logger log = LoggerFactory.getLogger(ConvertController.class);

    @Autowired
    private GeometryDataRepository geometryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomMongoRepository<OnSaveData> customMongoRepository;

    @Value("${batchSize:0}")
    private int batchSize;

    @EventListener(ApplicationReadyEvent.class)
    public void runMergeAfterStart() {
        if (batchSize <= 0) return;
        batchConvertDB(batchSize);
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
        var size = geometryRepository.countAllByCreateDateBeforeAndConvertedFalse(dateToConvert);
        log.info("Date to find converted rows : " + dateToConvert);


        var time = System.currentTimeMillis();
        int chunks = (int) Math.ceil(size / (double) batchSize);
        int currentChunk = 0;
        var hasNext = pullBatch(batchSize, dateToConvert, currentChunk, chunks);
        while (hasNext) {
            hasNext = pullBatch(batchSize, dateToConvert, ++currentChunk, chunks);
        }

        log.info("ok " + (System.currentTimeMillis() - time) + " ms");
        log.info("Date to find converted rows : " + dateToConvert);
        log.info("Table row count under time: " + size);

        return "ok " + (System.currentTimeMillis() - time) + " ms \nDate to find converted rows : " + dateToConvert;

    }

    private boolean pullBatch(int batchSize, Timestamp dateToConvert, int currentChunk, int chunks) {
        try {
            Thread.sleep(new Random().nextInt(10) + 15);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            log.info("STEP: " + currentChunk + "/" + chunks);
            Instant start = Instant.now();
            var page = geometryRepository.findAllByCreateDateBeforeAndConvertedFalse(dateToConvert, batchSize);

            log.info("1) Get from base " + Duration.between(start, start = Instant.now()).toMillis() + " ms; List size: " + page.size());

            if (page.isEmpty()) {
                return false;
            }

            customMongoRepository.saveAll(page.stream().parallel().map(GeometryData::convert).collect(Collectors.toList()));

            log.info("2) Saved in mongodb " + Duration.between(start, start = Instant.now()).toMillis() + " ms");

            geometryRepository.markAsConverted(page.stream().parallel().map(GeometryData::getUuid).collect(Collectors.toList()));

            log.info("3) Patch postgres " + Duration.between(start, Instant.now()).toMillis() + " ms");

        } catch (Exception ex) {
            log.error("STEP: " + currentChunk + "/" + chunks + " error:" + ex.getMessage());
            log.error("Date to find converted rows : " + dateToConvert);
            throw new RuntimeException(ex);
        }
        return true;
    }

}
