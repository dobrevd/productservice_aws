package com.dobrev.productservice.products.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class ProductsExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ProductException.class)
    protected ProductErrorResponse handelProductError(ProductException productException){
        var response = ProductErrorResponse.builder()
                .message(productException.getMessage())
                .statusCode(productException.getProductError().getHttpStatus().value())
                .requestId(ThreadContext.get("requestId"))
                .productId(productException.getProductsId())
                .build();

        log.error("{} with {}", productException.getProductError().getMessage(), productException.getProductsId());

        return response;
    }
}