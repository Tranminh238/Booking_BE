package com.example.demo.repository;

import com.example.demo.entity.SearchHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchHotelRepository extends JpaRepository<SearchHistory, Long> {

}
