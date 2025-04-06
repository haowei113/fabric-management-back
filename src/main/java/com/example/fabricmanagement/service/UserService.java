package com.example.fabricmanagement.service;

import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.model.User;
import com.example.fabricmanagement.repository.SupplierRepository;
import com.example.fabricmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user, String supplierName, String description) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 保存用户
        User savedUser = userRepository.save(user);

        // 如果是供应商，创建供应商信息
        if ("supplier".equals(user.getUserType())) {
            Supplier supplier = new Supplier();
            supplier.setSupplierName(supplierName);
            supplier.setDescription(description);
            supplier.setUser(savedUser);
            supplierRepository.save(supplier);
        }

        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}