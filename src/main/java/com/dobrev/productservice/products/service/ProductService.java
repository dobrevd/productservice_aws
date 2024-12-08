package com.dobrev.productservice.products.service;

import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.mapper.ProductMapper;
import com.dobrev.productservice.products.model.Product;
import com.dobrev.productservice.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        productRepository.getAll()
                .items()
                .subscribe(products::add)
                .join();
        return products;
    }

    public CompletableFuture<ProductDto> getProductById(String productId) {
        return productRepository.getById(productId)
                .thenApply(productMapper::toDto)
                .exceptionally(ex -> {
                    log.error("Error fetching product with ID: {}", productId, ex);
                    throw new NoSuchElementException(ex);
                });
    }

    public CompletableFuture<ProductDto> createProduct(ProductDto productDto) {
        var product = productMapper.toModel(productDto);
        product.setId(UUID.randomUUID().toString());

        return productRepository.create(product)
                .thenApply(voidResult -> {
                    log.info("Product is created with id: {}", product.getId());
                    return productMapper.toDto(product);
                });
    }

    public CompletableFuture<ProductDto> deleteProductById(String productId) {
        return productRepository.deleteById(productId)
                .thenApply(product -> {
                    log.info("Product is deleted with id: {}", product.getId());
                    return productMapper.toDto(product);
                })
                .exceptionally(ex -> {
                    log.error("Error deleting product with ID: {}", productId, ex);
                    throw new NoSuchElementException(ex);
                });
    }

    public CompletableFuture<ProductDto> updateProduct(ProductDto productDto, String productId) {
        var product = productMapper.toModel(productDto);

        return productRepository.update(product, productId)
                .thenApply(voidResult -> {
                    log.info("Product is updated with id: {}", product.getId());
                    return productMapper.toDto(product);
                })
                .exceptionally(ex -> {
                    log.error("Error updating product with ID: {}", productId, ex);
                    throw new NoSuchElementException(ex);
                });
    }
}