package com.nak.demo.service;

import com.nak.demo.common.config.ApplicationConfiguration;
import com.nak.demo.dto.base.PagenatedResponse;
import com.nak.demo.entity.Product;
import com.nak.demo.exception.model.DuplicateException;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.dto.product.ProductDto;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.dto.product.ProductResponseDto;
import com.nak.demo.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ApplicationConfiguration appConfig;

    public PagenatedResponse listProductWithPagination(Pageable pageable) {
        Page<Product> productPages = productRepository.findAll(pageable);
        Page<ProductResponseDto> productPagesDto = productPages.map(product -> mapper.toDto(product));
        return PagenatedResponse.from(productPagesDto, appConfig.getPagination().getUrlByResource("stock"));

    }

    public List<ProductResponseDto> listProduct() {
        List<Product> products = productRepository.findAll();

        List<ProductResponseDto> dtos = mapper.toDtoList(products);

       return mapper.toDtoList(products);

    }

    public ProductResponseDto getProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("product not found with id:" +productId));
            return mapper.toDto(product);
    }
    public void createProduct(ProductDto product) {
        //validate if product is already exists
        if (productRepository.existsByProductName(product.getName())) {
            throw new DuplicateException("product is already existed");
        }
        Product productEntity = mapper.toEntity(product);

        productRepository.save(productEntity);

    }
    public void updateProduct(Long productId, ProductDto payload) {
        Product existing = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("product not found with id :"  +productId));

        mapper.updateEntityFrom(existing,payload);

        productRepository.save(existing);

    }
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("product not found with id :" + productId);
        }
        productRepository.deleteById(productId);
    }
    public List<ProductResponseDto> searchProduct(String name, Double minPrice,Double maxPrice) {
        String formattedName = name != null ?
                name.toLowerCase()
                : name;
        List<Product> products = productRepository.findProductsWithFilters(formattedName, minPrice, maxPrice);
        return mapper.toDtoList( products);
    }
}

