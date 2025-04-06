package com.example.fabricmanagement.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String email;
    private String userType; // 'supplier' or 'regular'
    private String phone;

    // 仅当 userType 为 'supplier' 时需要
    private String supplierName;
    private String description;
}