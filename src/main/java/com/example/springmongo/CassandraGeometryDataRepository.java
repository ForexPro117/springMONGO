package com.example.springmongo;

import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CassandraGeometryDataRepository extends CassandraRepository<CassandraGeometryData, UUID> {
}
