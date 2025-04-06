package com.example.fabricmanagement.specification;

import com.example.fabricmanagement.model.Fabric;
import org.springframework.data.jpa.domain.Specification;

public class FabricSpecification {

    public static Specification<Fabric> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Fabric> hasMaterialId(Integer materialId) {
        return (root, query, criteriaBuilder) ->
                materialId == null ? null : criteriaBuilder.equal(root.get("material").get("materialId"), materialId);
    }

    public static Specification<Fabric> hasColorId(Integer colorId) {
        return (root, query, criteriaBuilder) ->
                colorId == null ? null : criteriaBuilder.isMember(colorId, root.get("colors").get("colorId"));
    }

    public static Specification<Fabric> hasSupplierId(Integer supplierId) {
        return (root, query, criteriaBuilder) ->
                supplierId == null ? null : criteriaBuilder.equal(root.get("supplier").get("supplierId"), supplierId);
    }
}