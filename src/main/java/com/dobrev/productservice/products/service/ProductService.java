package com.dobrev.productservice.products.service;

import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.events.dto.EventType;
import com.dobrev.productservice.products.events.dto.ProductEventDto;
import com.dobrev.productservice.products.events.service.EventsPublisher;
import com.dobrev.productservice.products.exceptions.ProductError;
import com.dobrev.productservice.products.exceptions.ProductException;
import com.dobrev.productservice.products.mapper.ProductMapper;
import com.dobrev.productservice.products.model.Product;
import com.dobrev.productservice.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final EventsPublisher eventsPublisher;

    public List<Product> getAllProducts() {
        List<Product> products = new CopyOnWriteArrayList<>();

        productRepository.getAll()
                .items()
                .subscribe(products::add)
                .join();
        return products;
    }

    public CompletableFuture<ProductDto> getProductById(String productId) {
        return productRepository.getById(productId)
                .thenApply(product -> {
                    if (product == null){
                        log.error("Product does not exist with id: {}", productId);
                        throw new ProductException(ProductError.PRODUCT_NOT_FOUND, productId);
                    }
                    return productMapper.toDto(product);
                })
                .exceptionally(ex -> {
                    log.error("Error fetching product with code: {}", productId, ex);
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<ProductDto> createProduct(ProductDto productDto) {
        var product = productMapper.toModel(productDto);
        product.setId(UUID.randomUUID().toString());

        var productCode = product.getCode();

        return productRepository.checkIfCodeExists(productCode)
                .thenCompose(existingProduct -> {
                    if (existingProduct != null) {
                        log.error("Product already exists with code: {}", productCode);
                        throw new ProductException(ProductError.PRODUCT_CODE_ALREADY_EXISTS, productCode);
                    }
                    return productRepository.create(product)
                            .thenApply(voidResult -> {
                                publishEvent(productMapper.toProductEvent(product),EventType.PRODUCT_CREATED, "created@email");

                                log.info("Product is created with id: {}", product.getId());
                                return productMapper.toDto(product);
                            });
                })
                .exceptionally(ex -> {
                    log.error("Error creating product: {}", productDto, ex);
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<ProductDto> updateProduct(ProductDto productDto, String productId) {
        var product = productMapper.toModel(productDto);
        var productCode = product.getCode();

        return productRepository.checkIfCodeExists(productCode)
                .thenCompose(existingProduct -> {
                    if (existingProduct != null && !existingProduct.getId().equals(productDto.id())) {
                        log.error("Can not update product with code: {}. Product already exists", productCode);
                        throw new ProductException(ProductError.PRODUCT_CODE_ALREADY_EXISTS, productCode);
                    }
                    return productRepository.update(product, productId)
                            .thenApply(voidResult -> {
                                publishEvent(productMapper.toProductEvent(product),
                                        EventType.PRODUCT_UPDATED,
                                        "update@email");
                                log.info("Product is updated with id: {}", product.getId());
                                return productMapper.toDto(product);
                            });
                })
                .exceptionally(ex -> {
                    log.error("Error updating product: {}", productDto, ex);
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<ProductDto> getByCode(String code) {
        return productRepository.getByCode(code)
                .thenApply(product -> {
                    if (product == null) {
                        throw new ProductException(ProductError.PRODUCT_NOT_FOUND, code);
                    }
                    return productMapper.toDto(product);
                })
                .exceptionally(ex -> {
                    log.error("Error fetching product with code: {}", code, ex);
                    throw new RuntimeException(ex);
                });
    }

    public CompletableFuture<ProductDto> deleteProductById(String productId) {
        return productRepository.deleteById(productId)
                .thenApply(product -> {
                    if(product == null){
                        log.error("Product with id:{} can not be deleted. It does not exist", productId);
                        throw new ProductException(ProductError.PRODUCT_NOT_FOUND, productId);
                    }
//                    publishEvent(productMapper.toProductEvent(product), EventType.PRODUCT_DELETED, "detele@email");

                    log.info("Product is deleted with id: {}", product.getId());
                    return productMapper.toDto(product);
                })
                .exceptionally(ex -> {
                    log.error("Error deleting product with id: {}", productId);
                    throw new RuntimeException(ex);
                });
    }

    private void publishEvent(ProductEventDto productEventDto, EventType eventType, String email){
        var publishResponse = eventsPublisher.sendProductEvents(productEventDto, eventType, email).join();
        ThreadContext.put("messageId", publishResponse.messageId());
    }
}