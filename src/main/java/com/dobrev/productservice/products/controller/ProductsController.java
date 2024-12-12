package com.dobrev.productservice.products.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.model.Product;
import com.dobrev.productservice.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@XRayEnabled
public class ProductsController {
    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts(){
        log.info("Get all products.");
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public CompletableFuture<ProductDto> getProductById(@PathVariable("id") String productId){
        log.info("Get product by it's id: {}", productId);
        return productService.getProductById(productId);
    }

    @PostMapping
    public CompletableFuture<ProductDto> creteProduct(@RequestBody ProductDto productDto){
        log.info("");
        return productService.createProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ProductDto> deleteProductById(@PathVariable("id")String productId){
        return productService.deleteProductById(productId);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ProductDto> updateProduct(@RequestBody ProductDto productDto,
                                                       @PathVariable("id") String productId){
        return productService.updateProduct(productDto, productId);
    }
}