package com.nak.demo.mapper;

import com.nak.demo.entity.Product;
import com.nak.demo.dto.product.ProductDto;
import com.nak.demo.dto.product.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    public Product toEntity(ProductDto dto){
        Product entity = new Product();

        entity.setProductName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());


        return entity;
    }
    public void updateEntityFrom(Product entity , ProductDto dto){
        if (entity == null || dto == null){
            return;
        }
        entity.setProductName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
    }

    public ProductResponseDto toDto(Product entity){
        ProductImageDto dto = new ProductImageDto();
        dto.setId(entity.getId());
        dto.setProductName(entity.getProductName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryName(entity.getCategory().getName());
        }
        dto.setTotalStock(entity.getTotalStock());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    public List<ProductResponseDto> toDtoList(List<Product> entities){
        if (entities == null || entities.isEmpty()){
            return new ArrayList<>();
        }
        return entities.stream()
                .map(product -> this.toDto(product))
                .collect(Collectors.toList());
    }
}
