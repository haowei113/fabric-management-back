package com.example.fabricmanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String userType; // 'supplier' or 'regular'

    private String phone;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Supplier supplier; // 供应商信息（仅当 userType 为 supplier 时有效）
}