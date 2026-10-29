package com.nak.demo.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nak.demo.dto.product.ProductResponseDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * ProductResponseDto plus the product's photo URL and category. Kept beside the mapper so the DTO itself didn't need editing;
 * you can move this field into ProductResponseDto later and delete this class.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ProductImageDto extends ProductResponseDto {
    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("category_name")
    private String categoryName;
}
