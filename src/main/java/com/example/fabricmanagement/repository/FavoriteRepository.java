package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.Favorite;
import com.example.fabricmanagement.model.User;
import com.example.fabricmanagement.model.Fabric;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByUserAndFabric(User user, Fabric fabric);
    List<Favorite> findByUser(User user);
    Page<Favorite> findByUser(User user, Pageable pageable); // 支持分页查询

}