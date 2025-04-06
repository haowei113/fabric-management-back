package com.example.fabricmanagement.service;

import com.example.fabricmanagement.dto.MaterialDTO;
import com.example.fabricmanagement.model.Material;
import com.example.fabricmanagement.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;

    @Transactional(readOnly = true)
    public List<MaterialDTO> getAllMaterials() {
        // 从数据库中获取所有材质的 ID 和名称
        return materialRepository.findAll().stream()
                .map(material -> new MaterialDTO(material.getMaterialId(), material.getMaterialName()))
                .collect(Collectors.toList());
    }
}