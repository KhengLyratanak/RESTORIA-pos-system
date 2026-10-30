package com.nak.demo.service;

import com.nak.demo.common.config.ApplicationConfiguration;
import com.nak.demo.dto.base.PagenatedResponse;
import com.nak.demo.entity.Category;
import com.nak.demo.entity.Product;
import com.nak.demo.repository.CategoryRepository;
import com.nak.demo.exception.model.DuplicateException;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.dto.product.ProductDto;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.dto.product.ProductResponseDto;
import com.nak.demo.mapper.ProductMapper;
import com.nak.demo.web.CloudinaryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nak.demo.exception.model.UnprocessableEntityException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ApplicationConfiguration appConfig;

    @Autowired
    private CloudinaryStorage cloudinary;

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

    // ----- product photos -----
    private static final long MAX_IMAGE_BYTES = 2L * 1024 * 1024;
    private static final Map<String, String> IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
    /** Folder the photos are written to; served at /uploads/** by UploadConfig. */
    public static final Path IMAGE_DIR = Paths.get("uploads", "products");

    public String saveImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id :" + productId));
        if (file == null || file.isEmpty()) {
            throw new UnprocessableEntityException("choose an image file");
        }
        String ext = file.getContentType() == null ? null : IMAGE_TYPES.get(file.getContentType());
        if (ext == null) {
            throw new UnprocessableEntityException("upload a JPG, PNG or WebP image");
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new UnprocessableEntityException("image must be 2 MB or smaller");
        }
        String baseName = productId + "-" + UUID.randomUUID();   // never trust the uploaded file name
        String fileName = baseName + ext;
        String newUrl;
        if (cloudinary.enabled()) {
            try {
                newUrl = cloudinary.upload(file.getBytes(), fileName, baseName);   // survives server restarts
            } catch (IOException e) {
                throw new IllegalStateException("could not read the image", e);
            }
        } else {
            try {
                Files.createDirectories(IMAGE_DIR);
                file.transferTo(IMAGE_DIR.resolve(fileName).toAbsolutePath());
            } catch (IOException e) {
                throw new IllegalStateException("could not save the image", e);
            }
            newUrl = "/uploads/products/" + fileName;
        }
        deleteImageFile(product.getImageUrl());   // replace: remove the old photo
        product.setImageUrl(newUrl);
        productRepository.save(product);
        return product.getImageUrl();
    }

    public void removeImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id :" + productId));
        deleteImageFile(product.getImageUrl());
        product.setImageUrl(null);
        productRepository.save(product);
    }

    private void deleteImageFile(String imageUrl) {
        if (cloudinary.isCloudinaryUrl(imageUrl)) {
            cloudinary.delete(imageUrl);
            return;
        }
        if (imageUrl == null || !imageUrl.startsWith("/uploads/products/")) return;
        String name = imageUrl.substring("/uploads/products/".length());
        if (name.contains("/") || name.contains("\\") || name.contains("..")) return;
        try {
            Files.deleteIfExists(IMAGE_DIR.resolve(name));
        } catch (IOException ignored) {
            // an old file left behind is harmless
        }
    }

    // ----- category -----
    /** Put a menu item in a category, or take it out of any category when categoryId is null. */
    public void setCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product not found with id :" + productId));
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("category not found with id :" + categoryId));
        }
        product.setCategory(category);
        productRepository.save(product);
    }
}
