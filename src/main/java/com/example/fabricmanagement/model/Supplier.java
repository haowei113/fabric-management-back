package com.example.fabricmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supplierId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 关联到 users 表

    @Column(nullable = false)
    private String supplierName;

    @Column(nullable = true)
    private String description;
}