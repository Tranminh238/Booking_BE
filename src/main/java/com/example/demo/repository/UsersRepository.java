package com.example.demo.repository;

import com.example.demo.dto.User.response.UserResponse;
import com.example.demo.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository <User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByUserId(Long userId);

    @Query(value = "SELECT new com.example.demo.dto.User.response.UserResponse(" +
       "u.id, CONCAT(u.firstName, ' ', u.lastName), u.email, u.phoneNumber, a.role) " +
       "FROM User u JOIN Account a ON u.userId = a.id",
    countQuery = "SELECT COUNT(u.id) FROM User u JOIN Account a ON u.userId = a.id")
    Page<UserResponse> findAllUser(Pageable pageable);

    @Query(value = "SELECT new com.example.demo.dto.User.response.UserResponse(" +
       "u.id, CONCAT(u.firstName, ' ', u.lastName), u.email, u.phoneNumber, a.role) " +
       "FROM User u JOIN Account a ON u.userId = a.id where a.isDeleted = 0",
    countQuery = "SELECT COUNT(u.id) FROM User u JOIN Account a ON u.userId = a.id where a.isDeleted = 0")
    Page<UserResponse> findAllUserActive(Pageable pageable);

    @Query(value = "SELECT new com.example.demo.dto.User.response.UserResponse(" +
       "u.id, CONCAT(u.firstName, ' ', u.lastName), u.email, u.phoneNumber, a.role) " +
       "FROM User u JOIN Account a ON u.userId = a.id where a.isDeleted = 1",
    countQuery = "SELECT COUNT(u.id) FROM User u JOIN Account a ON u.userId = a.id where a.isDeleted = 1")
    Page<UserResponse> findAllUserInactive(Pageable pageable);
}
