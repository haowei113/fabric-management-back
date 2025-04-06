package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.Material;
import com.example.fabricmanagement.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<Color, Integer> {
}

