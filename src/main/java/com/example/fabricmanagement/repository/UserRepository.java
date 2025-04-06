package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Integer userId);
    @Query("SELECT u.userId FROM User u WHERE u.username = :username")
    Optional<Integer> findUserIdByUsername(String username);

}