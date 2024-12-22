package com.dobrev.productservice.products.service;

import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.mapper.ProductMapper;
import com.dobrev.productservice.products.model.Product;
import com.dobrev.productservice.products.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @Test
    @Disabled
    void createProduct() {
        // given - precondition
        var productDto = ProductDto.builder()
                .productName("Product1")
                .code("Code1")
                .price(1.11F)
                .model("Model")
                .build();
        var product = Product.builder()
                .id("123")
                .productName("Product1")
                .code("Code1")
                .price(1.11F)
                .model("Model")
                .build();

        when(productRepository.create(product))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(productMapper.toModel(productDto)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);
        when(productRepository.checkIfCodeExists(anyString())).thenReturn(CompletableFuture.completedFuture(null));


        // when - action
        var actualResult = productService.createProduct(productDto).resultNow();

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(productDto);
    }
}