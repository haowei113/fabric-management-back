package com.example.fabricmanagement.controller;

import com.example.fabricmanagement.dto.FabricDTO;
import com.example.fabricmanagement.dto.FabricResponseDTO;
import com.example.fabricmanagement.model.Fabric;
import com.example.fabricmanagement.model.Material;
import com.example.fabricmanagement.model.Supplier;
import com.example.fabricmanagement.repository.FavoriteRepository;
import com.example.fabricmanagement.service.FabricService;
import com.example.fabricmanagement.repository.UserRepository;
import com.example.fabricmanagement.repository.FabricRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fabrics")
@RequiredArgsConstructor
public class FabricController {
    private final FabricService fabricService;
    private final UserRepository userRepository;
    private final FabricRepository fabricRepository;
    private final FavoriteRepository favoriteRepository;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FabricResponseDTO> addFabric(
            @RequestPart("data") String fabricData, // 接收 JSON 数据为字符串
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication auth) throws IOException {
        System.out.println("Incoming request: ");
        System.out.println("Fabric Data: " + fabricData);
        System.out.println("Image: " + (image != null ? image.getOriginalFilename() : "No image uploaded"));

        // 检查是否上传了图片
//        if (image != null && !image.isEmpty()) {
//            // 保存图片文件
//            String imagePath = saveImage(image);
//            System.out.println("Image saved at: " + imagePath);
//        }

        // 将 JSON 数据转换为 FabricDTO
        ObjectMapper objectMapper = new ObjectMapper();
        FabricDTO fabricDTO = objectMapper.readValue(fabricData, FabricDTO.class);


        // 获取用户名
        String username = (String) auth.getPrincipal();
        System.out.println("Username: " + username);

        // 根据用户名查询 user_id

        Integer userId = userRepository.findUserIdByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 调用服务层处理逻辑
        return ResponseEntity.ok(fabricService.addFabric(fabricDTO, image, userId));
    }
    @PostMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("Test");
        return ResponseEntity.ok("Test");
    }
    @GetMapping
    public ResponseEntity<List<FabricResponseDTO>> getAllFabrics() {
        return ResponseEntity.ok(fabricService.getAllFabrics());
    }
//    @GetMapping("/supplier/{supplierId}")
//    public ResponseEntity<List<FabricResponseDTO>> getFabricsBySupplier(@PathVariable Integer supplierId) {
//        return ResponseEntity.ok(fabricService.getFabricsBySupplier(supplierId));
//    }
//
//    @GetMapping("/material/{materialId}")
//    public ResponseEntity<List<FabricResponseDTO>> getFabricsByMaterial(@PathVariable Integer materialId) {
//        return ResponseEntity.ok(fabricService.getFabricsByMaterial(materialId));
//    }
//
//    @GetMapping("/color/{colorId}")
//    public ResponseEntity<List<FabricResponseDTO>> getFabricsByColor(@PathVariable Integer colorId) {
//        return ResponseEntity.ok(fabricService.getFabricsByColor(colorId));
//    }

    @DeleteMapping("/{fabricId}")
    public ResponseEntity<String> deleteFabric(@PathVariable Integer fabricId, Authentication auth) {
        // 检查用户是否已认证
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未认证");
        }
        // 获取用户名
        String username = (String) auth.getPrincipal();
        System.out.println("Authenticated user: " + username);
        // 调用服务层删除逻辑
        // 删除与该织物相关的所有收藏记录
        favoriteRepository.deleteByFabricId(fabricId);
        fabricService.deleteFabric(fabricId, username);
        return ResponseEntity.ok("织物已成功删除");
    }

    @PutMapping("/{fabricId}")
    public ResponseEntity<FabricResponseDTO> updateFabric(
            @PathVariable Integer fabricId,
            @RequestPart("data") String fabricData,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication auth) throws IOException {
        // 检查用户是否已认证
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未认证");
        }
        // 获取用户名
        String username = (String) auth.getPrincipal();
        System.out.println("Authenticated user: " + username);
        // 将 JSON 数据转换为 FabricDTO
        ObjectMapper objectMapper = new ObjectMapper();
        FabricDTO fabricDTO = objectMapper.readValue(fabricData, FabricDTO.class);
        // 调用服务层更新逻辑
        FabricResponseDTO updatedFabric = fabricService.updateFabric(fabricId, fabricDTO, image, username);
        return ResponseEntity.ok(updatedFabric);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FabricResponseDTO>> getFabrics(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer materialId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fabricId,asc") String sort) {
        // 调用服务层查询逻辑
        Page<FabricResponseDTO> fabrics = fabricService.getFabrics(name, materialId, colorId, supplierId, page, size, sort);
        return ResponseEntity.ok(fabrics);
    }


    // 收藏织物
    @PostMapping("/{fabricId}/favorite")
    public ResponseEntity<String> addFavorite(@PathVariable Integer fabricId, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未认证");
        }
        String username = (String) auth.getPrincipal();
        fabricService.addFavorite(fabricId, username);
        return ResponseEntity.ok("收藏成功");
    }

    // 取消收藏
    @DeleteMapping("/{fabricId}/favorite")
    public ResponseEntity<String> removeFavorite(@PathVariable Integer fabricId, Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未认证");
        }
        String username = (String) auth.getPrincipal();
        fabricService.removeFavorite(fabricId, username);
        return ResponseEntity.ok("取消收藏成功");
    }

    // 查看收藏列表
    @GetMapping("/favorites")
    public ResponseEntity<Page<FabricResponseDTO>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("用户未认证");
        }
        String username = (String) auth.getPrincipal();
        Page<FabricResponseDTO> favorites = fabricService.getFavorites(username, page, size);
        return ResponseEntity.ok(favorites);}

//    private String saveImage(MultipartFile image) throws IOException {
//        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
//        Path filePath = Paths.get("uploads", fileName);
//        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//        return "/uploads/" + fileName; // 返回图片的相对路径
//    }

    @GetMapping("/{fabricid}")
    public ResponseEntity<FabricResponseDTO> getFabricById(@PathVariable int fabricid) {
        // 根据 ID 查询面料信息
        Fabric fabric = fabricRepository.findById(fabricid)
                .orElseThrow(() -> new RuntimeException("面料不存在"));
        // 转换为 DTO
        FabricResponseDTO fabricResponseDTO = new FabricResponseDTO();
        fabricResponseDTO.setFabricId(fabric.getFabricId());
        fabricResponseDTO.setName(fabric.getName());
        fabricResponseDTO.setDescription(fabric.getDescription());
        fabricResponseDTO.setImagePath(fabric.getImagePath());

        // 设置材质名称
        if (fabric.getMaterial() != null) {
            fabricResponseDTO.setMaterialName(fabric.getMaterial().getMaterialName());
        }

        // 设置供应商名称
        if (fabric.getSupplier() != null) {
            fabricResponseDTO.setSupplierName(fabric.getSupplier().getSupplierName());
        }

        // 设置颜色名称列表
        if (fabric.getColors() != null) {
            fabricResponseDTO.setColorNames(fabric.getColors().stream()
                    .map(color -> color.getColorName())
                    .toList());
        }

        return ResponseEntity.ok(fabricResponseDTO);
    }
}

