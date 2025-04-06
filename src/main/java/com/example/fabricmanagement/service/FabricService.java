package com.example.fabricmanagement.service;

import java.io.IOException;

import com.example.fabricmanagement.dto.FabricDTO;
import com.example.fabricmanagement.dto.FabricResponseDTO;
import com.example.fabricmanagement.model.*;
import com.example.fabricmanagement.repository.*;
import com.example.fabricmanagement.specification.FabricSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FabricService {
    private final FabricRepository fabricRepository;
    private final MaterialRepository materialRepository;
    private final SupplierRepository supplierRepository;
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final FavoriteRepository favoriteRepository;
    @Transactional
    public FabricResponseDTO addFabric(FabricDTO fabricDTO, MultipartFile image,Integer userId) throws IOException {
        // 1. 验证供应商
        Supplier supplier = supplierRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("供应商身份未注册"));

        // 2. 处理图片上传
        String imagePath = null;
        if (image != null && !image.isEmpty()) {
            imagePath = fileStorageService.storeFile(image);
        }

        // 3. 创建面料记录
        Fabric fabric = new Fabric();
        fabric.setName(fabricDTO.getName());
        fabric.setDescription(fabricDTO.getDescription());
        fabric.setMaterial(materialRepository.findById(fabricDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("材质不存在")));
        fabric.setSupplier(supplier);
        fabric.setImagePath(imagePath);

        // 4. 处理颜色关联
        if (fabricDTO.getColorIds() != null) {
            Set<Color> colors = colorRepository.findAllById(fabricDTO.getColorIds())
                    .stream()
                    .collect(Collectors.toSet());
            fabric.setColors(colors);
        }

        return convertToResponseDTO(fabricRepository.save(fabric));

    }

    public List<FabricResponseDTO> getAllFabrics() {
        return fabricRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

//    public List<FabricResponseDTO> getFabricsBySupplier(Integer supplierId) {
//        Supplier supplier = supplierRepository.findById(supplierId)
//                .orElseThrow(() -> new RuntimeException("Supplier not found"));
//
//        return fabricRepository.findBySupplier(supplier).stream()
//                .map(this::convertToResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<FabricResponseDTO> getFabricsByMaterial(Integer materialId) {
//        Material material = materialRepository.findById(materialId)
//                .orElseThrow(() -> new RuntimeException("Material not found"));
//
//        return fabricRepository.findByMaterial(material).stream()
//                .map(this::convertToResponseDTO)
//                .collect(Collectors.toList());
//    }
//
//    public List<FabricResponseDTO> getFabricsByColor(Integer colorId) {
//        Color color = colorRepository.findById(colorId)
//                .orElseThrow(() -> new RuntimeException("Color not found"));
//
//        return fabricRepository.findByColorsContaining(color).stream()
//                .map(this::convertToResponseDTO)
//                .collect(Collectors.toList());
//    }

    private FabricResponseDTO convertToResponseDTO(Fabric fabric) {
        FabricResponseDTO dto = new FabricResponseDTO();
        dto.setFabricId(fabric.getFabricId());
        dto.setName(fabric.getName());
        dto.setMaterialName(fabric.getMaterial().getMaterialName());
        dto.setSupplierName(fabric.getSupplier().getSupplierName());
        dto.setDescription(fabric.getDescription());
        dto.setImagePath(fabric.getImagePath());

        if (fabric.getColors() != null) {
            dto.setColorNames(fabric.getColors().stream()
                    .map(Color::getColorName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    @Transactional
    public void deleteFabric(Integer fabricId, String username) {
        // 验证织物是否存在
        Fabric fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new RuntimeException("织物不存在"));
        // 根据用户名查询用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 验证用户是否是该面料的供应商
        if (!fabric.getSupplier().getUser().equals(user)) {
            throw new RuntimeException("您无权删除此织物");
        }
        // 删除织物对应的图片文件
        if (fabric.getImagePath() != null) {
            try {
                // 将相对路径转换为绝对路径
                Path filePath = Paths.get("uploads").resolve(fabric.getImagePath()).toAbsolutePath();
                Files.deleteIfExists(filePath); // 删除文件
                System.out.println("图片文件已删除: " + fabric.getImagePath());
            } catch (IOException e) {
                System.err.println("删除图片文件失败: " + fabric.getImagePath());
                e.printStackTrace();
            }
        }
        // 删除织物
        fabricRepository.delete(fabric);
        System.out.println("织物已删除，ID: " + fabricId);
    }

    @Transactional
    public FabricResponseDTO updateFabric(Integer fabricId, FabricDTO fabricDTO, MultipartFile image, String username) throws IOException {
        // 验证织物是否存在
        Fabric fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new RuntimeException("织物不存在"));
        // 根据用户名查询用户信息
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        // 验证用户是否是该面料的供应商
        Supplier supplier = fabric.getSupplier();
        if (supplier == null || !supplier.getUser().equals(user)) {
            throw new RuntimeException("您无权修改此织物");
        }
        // 更新面料信息
        fabric.setName(fabricDTO.getName());
        fabric.setDescription(fabricDTO.getDescription());
        fabric.setMaterial(materialRepository.findById(fabricDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("材质不存在")));
        // 更新颜色关联
        if (fabricDTO.getColorIds() != null) {
            Set<Color> colors = colorRepository.findAllById(fabricDTO.getColorIds())
                    .stream()
                    .collect(Collectors.toSet());
            fabric.setColors(colors);
        }
        // 如果上传了新的图片，更新图片路径
        if (image != null && !image.isEmpty()) {
            String imagePath = storeFile(image); // 调用文件存储方法
            fabric.setImagePath(imagePath);
        }
        // 保存更新后的面料
        Fabric updatedFabric = fabricRepository.save(fabric);
        // 转换为响应 DTO
        return convertToResponseDTO(updatedFabric);
    }
    private String storeFile(MultipartFile file) {
        // 调用 fileStorageService 存储文件
        return fileStorageService.storeFile(file);
    }

    @Transactional(readOnly = true)
    public Page<FabricResponseDTO> getFabrics(String name, Integer materialId, Integer colorId, Integer supplierId, int page, int size, String sort) {
        // 解析排序参数
        String[] sortParams = sort.split(",");
        Sort sortOrder = Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]);
        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        // 动态查询条件
        Specification<Fabric> spec = Specification.where(FabricSpecification.hasName(name))
                .and(FabricSpecification.hasMaterialId(materialId))
                .and(FabricSpecification.hasColorId(colorId))
                .and(FabricSpecification.hasSupplierId(supplierId));
        // 调用存储库查询
        Page<Fabric> fabrics = fabricRepository.findAll(spec, pageable);
        // 转换为响应 DTO
        return fabrics.map(this::convertToResponseDTO);
    }

    @Transactional
    public void addFavorite(Integer fabricId, String username) {
        // 获取用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取织物
        Fabric fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new RuntimeException("织物不存在"));

        // 检查是否已收藏
        if (favoriteRepository.findByUserAndFabric(user, fabric).isPresent()) {
            throw new RuntimeException("该织物已收藏");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setFabric(fabric);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Integer fabricId, String username) {
        // 获取用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取织物
        Fabric fabric = fabricRepository.findById(fabricId)
                .orElseThrow(() -> new RuntimeException("织物不存在"));

        // 检查是否已收藏
        Favorite favorite = favoriteRepository.findByUserAndFabric(user, fabric)
                .orElseThrow(() -> new RuntimeException("该织物未收藏"));

        // 删除收藏
        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public Page<FabricResponseDTO> getFavorites(String username, int page, int size) {
        // 获取用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size);

        // 查询收藏记录
        Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);

        // 转换为响应 DTO
        return favorites.map(favorite -> convertToResponseDTO(favorite.getFabric()));

    }

}