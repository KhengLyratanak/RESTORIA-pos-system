package com.nak.demo.mapper;


import com.nak.demo.dto.category.CategoryDto;
import com.nak.demo.dto.category.CategoryResponseDto;
import com.nak.demo.entity.Category;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponseDto toResponeDto(Category entity){
        CategoryResponseDto dto = new CategoryResponseDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Category toEntity(CategoryDto categoryDto){
        Category entity = new Category();
        entity.setName(categoryDto.getName());
        entity.setDescription(categoryDto.getDescription());
        return entity;
    }

    public void toUpdateDto(Category entity, CategoryDto dto){
        if (entity ==null || dto==null){
            return;
        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
    }
    public List<CategoryResponseDto> toDtoList(List<Category> entities){
        if (entities==null || entities.isEmpty()){
            return new ArrayList<>();
        }
        return entities.stream()
                .map(category -> this.toResponeDto(category))
                .collect(Collectors.toList());
    }
}
