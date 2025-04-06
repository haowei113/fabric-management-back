package com.example.fabricmanagement.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer materialId;

    @Column(unique = true)
    private String materialName;

    private String description;
}