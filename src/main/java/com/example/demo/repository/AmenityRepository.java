package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Amenity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    @Query("SELECT a FROM Amenity a WHERE LOWER(a.type) = LOWER(:type)")
    List<Amenity> findByType(@Param("type") String type);
}
