package com.example.fabricmanagement.service;

import com.example.fabricmanagement.dto.ColorDTO;
import com.example.fabricmanagement.model.Color;
import com.example.fabricmanagement.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;

    @Transactional(readOnly = true)
    public List<ColorDTO> getAllColors() {
        // 从数据库中获取所有颜色的 ID 和名称
        return colorRepository.findAll().stream()
                .map(color -> new ColorDTO(color.getColorId(), color.getColorName()))
                .collect(Collectors.toList());
    }
}