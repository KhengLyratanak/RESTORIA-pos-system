package com.nak.demo.repository;

import com.nak.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p WHERE" +
            "(:name IS NULL OR LOWER(p.productName) LIKE %:name% ) AND" +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND" +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)"
    )
    List<Product> findProductsWithFilters(
            @Param("name") String name,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
    Boolean existsByProductName(String productName);

    /** Takes every menu item out of a category (used before the category is deleted). */
    @Modifying
    @Query("UPDATE Product p SET p.category = null WHERE p.category.id = :categoryId")
    int clearCategory(@Param("categoryId") Long categoryId);
}
