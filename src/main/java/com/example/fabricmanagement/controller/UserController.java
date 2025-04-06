package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.model.User;
import com.example.fabricmanagement.repository.SupplierRepository;
import com.example.fabricmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        // 从 SecurityContext 中获取当前登录用户的用户名或 ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 假设用户名是唯一标识

        // 根据用户名查询用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 构建返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("userid", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("usertype", user.getUserType()); // 包含用户类型信息

        // 如果用户类型是供应商，查询供应商信息
        if ("supplier".equalsIgnoreCase(user.getUserType())) {
            Supplier supplier = supplierRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("供应商信息不存在"));
            response.put("supplierid", supplier.getSupplierId());
            response.put("suppliername", supplier.getSupplierName());
            response.put("description", supplier.getDescription());
        }

        return ResponseEntity.ok(response);
    }
}