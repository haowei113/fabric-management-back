package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.model.Material;
import com.example.fabricmanagement.model.Color;
import com.example.fabricmanagement.repository.ColorRepository;
import com.example.fabricmanagement.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system-data")
@RequiredArgsConstructor
public class SystemDataController {

    private final MaterialRepository materialRepository;
    private final ColorRepository colorRepository;

    @GetMapping("/materials")
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    @GetMapping("/colors")
    public List<Color> getAllColors() {
        return colorRepository.findAll();
    }
}