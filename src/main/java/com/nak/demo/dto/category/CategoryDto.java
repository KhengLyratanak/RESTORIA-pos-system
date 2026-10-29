package com.nak.demo.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    @NotNull(message = "category name is required")
    private String name ;

    @Size(max = 400,message = "description cant be exceeded 400 charectors")
    private String description;
}
