package com.example.fabricmanagement.model;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "colors")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorId;

    @Column(unique = true)
    private String colorName;

    private String hexCode;
}