package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.AmenityCategory;
import java.util.List;

@Repository
public interface AmenityCategoryRepository extends JpaRepository<AmenityCategory, Long> {
    List<AmenityCategory> findAll();
    boolean existsByName(String name);
}
