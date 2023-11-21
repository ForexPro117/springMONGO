package com.example.springmongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class CustomMongoRepository<T extends GeometryData> {

    @Autowired
    private GridFsTemplate gridFsTemplate;


    public void saveAll(Iterable<T> entities) {
        StreamSupport.stream(entities.spliterator(), true).forEach(this::save);
    }

    public void save(T entity) {
        DBObject metaData = new BasicDBObject();
        metaData.put("_id", entity.getUuid());
        try (InputStream stream = new ByteArrayInputStream(SerializationUtils.serialize(entity))) {
            gridFsTemplate.store(stream, entity.getUuid().toString(), metaData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(UUID uuid) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(new Query(Criteria.where("metadata._id").is(uuid)));
        return Optional.of(deserialize(gridFsFile));
    }

    public List<T> findAll(List<UUID> uuids) {
        List<GridFSFile> gridFSFiles = new ArrayList<>();
        gridFsTemplate.find(new Query(Criteria.where("metadata._id").in(uuids))).into(gridFSFiles);
        return gridFSFiles.stream().parallel().map(this::deserialize).collect(Collectors.toList());
    }

    public void deleteAll(List<UUID> uuids) {
        gridFsTemplate.delete(new Query(Criteria.where("metadata._id").nin(uuids)));
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
