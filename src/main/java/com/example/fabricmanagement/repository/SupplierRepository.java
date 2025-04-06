package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Object> findByUser(User user);
    @Query("SELECT s FROM Supplier s WHERE s.user.userId = :userId")
    Optional<Supplier> findByUserId(@Param("userId") Integer userId);
}