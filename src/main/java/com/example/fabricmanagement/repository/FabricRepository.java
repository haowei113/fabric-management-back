package com.example.fabricmanagement.repository;

import com.example.fabricmanagement.model.Fabric;
import com.example.fabricmanagement.model.Material;
import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FabricRepository extends JpaRepository<Fabric, Integer>, JpaSpecificationExecutor<Fabric> {
    List<Fabric> findBySupplier(Supplier supplier);
    List<Fabric> findByMaterial(Material material);
    List<Fabric> findByColorsContaining(Color color);
}