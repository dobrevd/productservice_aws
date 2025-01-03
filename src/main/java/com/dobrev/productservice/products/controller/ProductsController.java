package com.dobrev.productservice.products.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@XRayEnabled
public class ProductsController {
    private final ProductService productService;

    @GetMapping
    public Object getAllProducts(@RequestParam(required = false) String code){
        if (code != null){
            log.info("Get product by code {}", code);
            return productService.getByCode(code);
        }
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
        log.info("Product is created");
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