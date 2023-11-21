package com.example.springmongo.repository;

import com.example.springmongo.CassandraGeometryData;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface CassandraGeometryDataRepository extends CassandraRepository<CassandraGeometryData, UUID> {

}
