package com.dobrev.productservice.products.events.dto;

public record ProductEventDto(
    String id,
    String code,
    String email,
    float price
) { }