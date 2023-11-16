package com.example.springmongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface PGeometryDataRepository extends JpaRepository<GeometryData, UUID> {

    Page<GeometryData> findAllByCreateDateBefore(Timestamp date , Pageable pageable);

}
