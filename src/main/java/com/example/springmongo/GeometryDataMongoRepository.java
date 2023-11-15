package com.example.springmongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeometryDataMongoRepository extends MongoRepository<MongoGeometryData, UUID> {
}
