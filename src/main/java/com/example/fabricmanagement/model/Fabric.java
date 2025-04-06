package com.example.fabricmanagement.model;

import lombok.Data;
import jakarta.persistence.*;

import java.awt.*;
import java.util.Set;

@Data
@Entity
@Table(name = "fabrics")
public class Fabric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fabricId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    private String description;
    private String imagePath;

    @ManyToMany
    @JoinTable(
            name = "fabric_colors",
            joinColumns = @JoinColumn(name = "fabric_id"),
            inverseJoinColumns = @JoinColumn(name = "color_id")
    )
    private Set<Color> colors;
}