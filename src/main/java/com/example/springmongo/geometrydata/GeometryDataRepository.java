package com.example.springmongo.geometrydata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface GeometryDataRepository extends PagingAndSortingRepository<GeometryData, UUID> {

    Page<GeometryData> findAllByCreateDateBeforeAndConvertedFalse(Timestamp date, Pageable pageable);

    Long countAllByCreateDateBeforeAndConvertedFalse(Timestamp date);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "update geometry_data set converted = true where uuid in :uuids", nativeQuery = true)
    void markAsConverted(@Param("uuids") List<UUID> uuids);

}
