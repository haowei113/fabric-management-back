package com.example.fabricmanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class FabricResponseDTO {
    private Integer fabricId;
    private String name;
    private String materialName;
    private String supplierName;
    private String description;
    private String imagePath;
    private List<String> colorNames;
}