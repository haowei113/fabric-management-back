package com.example.fabricmanagement.service;

import com.example.fabricmanagement.dto.SupplierDTO;
import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        // 从数据库中获取所有供应商的 ID 和名称
        return supplierRepository.findAll().stream()
                .map(supplier -> new SupplierDTO(supplier.getSupplierId(), supplier.getSupplierName()))
                .collect(Collectors.toList());
    }
}