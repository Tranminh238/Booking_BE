package com.example.demo.repository;

import com.example.demo.dto.User.response.UserResponse;
import com.example.demo.entity.User;
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

    @Query("SELECT new com.example.demo.dto.User.response.UserResponse(" +
       "u.id, CONCAT(u.firstName, ' ', u.lastName), u.email, u.phoneNumber, a.role) " +
       "FROM User u JOIN Account a ON u.id = a.id")
    List<UserResponse> findAllUser();
}
