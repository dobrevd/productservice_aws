package com.dobrev.productservice.products.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductEventDto(
    String id,
    String code,
    String email,
    float price
) {}