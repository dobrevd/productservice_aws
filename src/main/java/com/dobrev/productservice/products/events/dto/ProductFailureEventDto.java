package com.dobrev.productservice.products.events.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record ProductFailureEventDto(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String id,
        String error,
        String email,
        int status
) { }