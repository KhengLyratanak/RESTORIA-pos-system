package com.nak.demo.controller;

import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.product.ProductResponseDto;
import com.nak.demo.dto.product.ProductDto;
import com.nak.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/paginated")
    public ResponseEntity<Response> listProductWithPagination(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<ProductResponseDto> products = productService.listProductWithPagination(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved product with pagination",products ));
    }

    @GetMapping()
    public ResponseEntity<Response> listProduct() {
        List<ProductResponseDto> dtos = productService.listProduct();
         return ResponseEntity.status(HttpStatus.OK)
                 .body(Response.success("200","success","successfully retrieved product",dtos ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProduct(@PathVariable("id") Long productId) {
        ProductResponseDto product = productService.getProduct(productId);
         return ResponseEntity.status(HttpStatus.OK)
                 .body(Response.success("200","success","successfully product founded",product));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> seaerchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice
    ) {
            List<ProductResponseDto> products = productService.searchProduct
                    (name,minPrice,maxPrice);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved products with filter",products));
    }

    @PostMapping()
    public ResponseEntity<Response> createProduct( @Valid @RequestBody ProductDto payload) {
         productService.createProduct(payload);
         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(Response.success("201","success","successfully created product"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> updateProduct(@PathVariable("id") Long productId, @RequestBody ProductDto payload) {
         productService.updateProduct(productId, payload);
         return ResponseEntity.status(HttpStatus.OK)
                 .body(Response.success("200","success","successfully updated product id:",productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable("id") Long productId) {
         productService.deleteProduct(productId);
         return ResponseEntity.status(HttpStatus.OK)
                 .body(Response.success("200","success","successfully deleted product"));
    }
}