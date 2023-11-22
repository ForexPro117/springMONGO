package com.example.springmongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface PGeometryDataRepository extends JpaRepository<GeometryData, UUID> {

    Page<GeometryData> findAllByCreateDateBeforeAndConvertedFalse(Timestamp date, Pageable pageable);

    Long countAllByCreateDateBeforeAndConvertedFalse(Timestamp date);

    @Transactional
    @Modifying
    @Query(value = "update GeometryData d set d.converted = true where d.uuid in :uuids")
    void markAsConverted(List<UUID> uuids);

}
