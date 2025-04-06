package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.dto.ColorDTO;
import com.example.fabricmanagement.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<List<ColorDTO>> getAllColors() {
        // 调用服务层获取所有颜色名称及 ID
        List<ColorDTO> colors = colorService.getAllColors();
        return ResponseEntity.ok(colors);
    }
}