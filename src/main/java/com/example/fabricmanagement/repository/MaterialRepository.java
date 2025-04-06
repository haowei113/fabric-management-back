package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {

}