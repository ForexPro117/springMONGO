package com.example.springmongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeometryDataRepository extends MongoRepository<MongoGeometryDataBin, UUID> {
}
