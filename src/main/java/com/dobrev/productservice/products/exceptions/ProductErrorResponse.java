package com.dobrev.productservice.products.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record ProductErrorResponse(
        String message,
        int statusCode,
        String requestId,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String productId
) {}
