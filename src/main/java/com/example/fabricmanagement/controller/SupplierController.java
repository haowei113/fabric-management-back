package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.dto.SupplierDTO;
import com.example.fabricmanagement.repository.SupplierRepository;
import com.example.fabricmanagement.service.SupplierService;
import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.model.User;
import com.example.fabricmanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final SupplierRepository supplierRepository;

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        // 调用服务层获取所有供应商名称及 ID
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Integer supplierId) {
        // 根据供应商 ID 查询供应商信息
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("供应商不存在"));

        // 构建返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("supplierId", supplier.getSupplierId());
        response.put("supplierName", supplier.getSupplierName());
        response.put("description", supplier.getDescription());

        // 获取关联的用户信息
        User user = supplier.getUser();
        if (user != null) {
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("phone", user.getPhone());
        }

        return ResponseEntity.ok(response);
    }
}