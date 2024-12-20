package com.dobrev.productservice.products.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ProductError {
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_CODE_ALREADY_EXISTS("Product code already exists", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus httpStatus;
}