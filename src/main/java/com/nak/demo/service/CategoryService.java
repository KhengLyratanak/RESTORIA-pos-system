package com.nak.demo.service;

import com.nak.demo.dto.category.CategoryDto;
import com.nak.demo.dto.category.CategoryResponseDto;
import com.nak.demo.entity.Category;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.mapper.CategoryMapper;
import com.nak.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper mapper;

    public void CreateCategory(CategoryDto payload){
        if (categoryRepository.existsByName(payload.getName()))
            throw new ResourceNotFoundException("category name is existed");
        Category category = mapper.toEntity(payload);

        categoryRepository.save(category);
    }
    public List<CategoryResponseDto> listCategory(){
        List<Category> categories = categoryRepository.findAll();

        return  mapper.toDtoList(categories);
    }
    public void updateCategory(CategoryDto payload,Long categoryId){
        Category existing = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("category not found with id :" +categoryId));
        mapper.toUpdateDto(existing,payload);
        categoryRepository.save(existing);
    }
    public void deleteCategory(Long categoryId ){
        if (!categoryRepository.existsById(categoryId))
            throw new ResourceNotFoundException("category not found with id :"+categoryId);
        categoryRepository.deleteById(categoryId);
    }

}
