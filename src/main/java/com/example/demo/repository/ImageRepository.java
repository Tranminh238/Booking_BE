package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Image;
import com.example.demo.enums.ImageEmun.RefType;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    // Lấy tất cả ảnh theo refId + refType (Hotel / Room)
    List<Image> findByRefIdAndRefType(Long refId, RefType refType);

    // Xoá tất cả ảnh theo refId + refType
    void deleteByRefIdAndRefType(Long refId, RefType refType);

    // Kiểm tra có ảnh hay không
    boolean existsByRefIdAndRefType(Long refId, RefType refType);
}