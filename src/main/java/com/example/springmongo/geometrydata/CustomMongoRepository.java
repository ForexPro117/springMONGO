package com.example.springmongo.geometrydata;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.BsonBinary;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomMongoRepository<T extends OnSaveData> {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MongoDatabaseFactory dbFactory;

    private GridFSBucket getGridFs() {
        MongoDatabase db = this.dbFactory.getMongoDatabase();
        return GridFSBuckets.create(db);
    }


    public void saveAll(Collection<T> entities) {
        entities.parallelStream().forEach(this::save);
    }

//    public void save(T entity) {
//        DBObject metaData = new BasicDBObject();
//        metaData.put("_id", entity.getUuid());
//        metaData.put("hashcode", entity.getHashCode());
//        try (InputStream stream = new ByteArrayInputStream(SerializationUtils.serialize(entity))) {
//            gridFsTemplate.store(stream, entity.getUuid().toString(), metaData);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void save(T entity) {
        try (InputStream stream = new ByteArrayInputStream(SerializationUtils.serialize(entity))) {
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(261120)
                    .metadata(new Document("hashcode", entity.getHashCode()));
            getGridFs().uploadFromStream(new BsonBinary(entity.getUuid()), entity.getUuid().toString(), stream, options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(UUID uuid) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(uuid)));
        return Optional.of(deserialize(gridFsFile));
    }

    public List<T> findAll(List<UUID> uuids) {
        List<GridFSFile> gridFSFiles = new ArrayList<>();
        gridFsTemplate.find(new Query(Criteria.where("_id").in(uuids))).into(gridFSFiles);
        return gridFSFiles.stream().parallel().map(this::deserialize).collect(Collectors.toList());
    }

    public void deleteAll(List<UUID> uuids) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").in(uuids)));
    }

    private T deserialize(GridFSFile file) {
        byte[] obj;
        try {
            obj = gridFsTemplate.getResource(file).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (T) SerializationUtils.deserialize(obj);
    }

}
