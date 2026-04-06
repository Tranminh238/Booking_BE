package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository <User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> findByUserId(Long userId);
}
