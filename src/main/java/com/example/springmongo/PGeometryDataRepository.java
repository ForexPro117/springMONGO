package com.example.springmongo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PGeometryDataRepository extends JpaRepository<GeometryData, UUID> {

    List<GeometryData> findTop100By();
}
