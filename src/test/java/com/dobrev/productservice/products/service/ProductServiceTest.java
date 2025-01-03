package com.dobrev.productservice.products.service;

import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.events.dto.EventType;
import com.dobrev.productservice.products.events.dto.ProductEventDto;
import com.dobrev.productservice.products.events.service.EventsPublisher;
import com.dobrev.productservice.products.mapper.ProductMapper;
import com.dobrev.productservice.products.model.Product;
import com.dobrev.productservice.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private EventsPublisher eventsPublisher;


//    private static final int PAGE_SIZE = 100;

//    @BeforeEach
//    void setup() throws NoSuchFieldException, IllegalAccessException {
//        Field pageSizeField = ProductRepository.class.getDeclaredField("pageSize");
//        pageSizeField.setAccessible(true);
//        pageSizeField.set(productRepository, PAGE_SIZE);
//    }
    //    @Disabled
    @Test
    void createProduct() {
        // given - precondition
        var productDto = ProductDto.builder()
                .productName("Product1")
                .code("Code11")
                .price(11.11F)
                .model("Model1")
                .build();
        var product = Product.builder()
                .id("123")
                .productName("Product1")
                .code("Code1")
                .price(1.11F)
                .model("Model")
                .build();

        when(productMapper.toModel(productDto)).thenReturn(product);
        when(productRepository.checkIfCodeExists(anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(productRepository.create(product))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(productMapper.toDto(product)).thenReturn(productDto);
        when(productMapper.toProductEvent(product)).thenReturn(ProductEventDto.builder().build());
        when(eventsPublisher.sendProductEvents(any(ProductEventDto.class),any(EventType.class), anyString()))
                .thenReturn(CompletableFuture.completedFuture(PublishResponse.builder().build()));

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