package com.nak.demo.controller;


import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.category.CategoryDto;
import com.nak.demo.dto.category.CategoryResponseDto;
import com.nak.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v10/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Response> createCategory(@RequestBody CategoryDto payload) {
        categoryService.CreateCategory(payload);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Response.success("201","success","successfullly created category"));
    }

    @GetMapping
    public ResponseEntity<Response> listCategory(CategoryResponseDto payload){
        List<CategoryResponseDto> categories = categoryService.listCategory();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","sucess","successfully retrieved category",categories));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCategory(@RequestBody CategoryDto payload, @PathVariable ("id") Long categoryId){
        categoryService.updateCategory(payload,categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully updated category with id :",categoryId));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCategory (@PathVariable ("id") Long categoryId){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully deleted category with id : ",categoryId));

    }
}
