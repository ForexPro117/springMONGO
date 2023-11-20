package com.example.springmongo;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface CassandraGeometryDataRepository extends CassandraRepository<CassandraGeometryData, UUID> {
}
