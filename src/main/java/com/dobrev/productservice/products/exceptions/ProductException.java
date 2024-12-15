package com.dobrev.productservice.products.exceptions;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ProductException extends RuntimeException{
    private final ProductError productError;
    @Nullable
    private final String productsId;
}