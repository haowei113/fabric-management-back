package com.example.fabricmanagement.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class FabricDTO {
    @NotBlank(message = "织物名称不能为空")
    private String name; // 供应商自定义名称

    private String description; // 供应商自定义描述

    @NotNull(message = "必须选择材质")
    private Integer materialId; // 从系统选择（单选）

    @NotEmpty(message = "至少选择一种颜色")
    private List<Integer> colorIds; // 从系统选择（多选）

//    private MultipartFile image; // 上传的图片文件
}