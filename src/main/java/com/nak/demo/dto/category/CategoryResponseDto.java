package com.nak.demo.dto.category;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"id","name","description","created_at","updated_at"})
public class CategoryResponseDto {
    @JsonProperty("category_id")
    private Long id ;

    private String name;

    private String description;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

}
