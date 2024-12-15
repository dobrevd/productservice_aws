package com.dobrev.productservice.products.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductError {
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;
}