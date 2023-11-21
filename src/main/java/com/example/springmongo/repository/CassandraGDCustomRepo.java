package com.example.springmongo.repository;

import com.example.springmongo.CassandraGeometryData;
import com.example.springmongo.GDByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

@Component
public class CassandraGDCustomRepo {

    @Autowired
    private CassandraTemplate template;

    public CassandraGeometryData save(CassandraGeometryData entity) {
        GDByteBuffer newEntity = new GDByteBuffer(entity);
        if (newEntity.getUuid() == null) newEntity.setUuid(UUID.randomUUID());
        newEntity = template.insert(newEntity);
        return newEntity.getGeometryData();
    }

    public List<CassandraGeometryData> saveAll(Iterable<CassandraGeometryData> entity) {
        List<CassandraGeometryData> result = new ArrayList<>();
        StreamSupport.stream(entity.spliterator(), false).parallel().forEach(el -> result.add(save(el)));
        return result;
    }

    public CassandraGeometryData findById(UUID uuid) {
        String sql = "SELECT * FROM geometry_data WHERE uuid = " + uuid;
        return template.selectOne(sql, GDByteBuffer.class).getGeometryData();
    }

    public Set<CassandraGeometryData> findAllById(Iterable<UUID> uuids) {
        String sql = "SELECT * FROM geometry_data WHERE uuid in (";
        StringJoiner uuidsStr = new StringJoiner(", ");
        for (UUID uuid : uuids) {
            uuidsStr.add(uuid.toString());
        }
        sql += uuidsStr + ")";

        return template.select(sql, GDByteBuffer.class).stream().map(GDByteBuffer::getGeometryData)
                .collect(Collectors.toSet());
    }
}
