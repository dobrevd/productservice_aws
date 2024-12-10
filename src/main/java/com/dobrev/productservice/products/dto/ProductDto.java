package com.dobrev.productservice.products.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record ProductDto(
         String id,
         String productName,
         String code,
         float price,
         String model,
         @JsonInclude(JsonInclude.Include.NON_NULL)
         String url
) {}