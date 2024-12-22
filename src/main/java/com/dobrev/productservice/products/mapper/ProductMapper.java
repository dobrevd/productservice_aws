package com.dobrev.productservice.products.mapper;

import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.events.dto.ProductEventDto;
import com.dobrev.productservice.products.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    Product toModel(ProductDto productDto);

    ProductDto toDto(Product product);

    ProductEventDto toProductEvent(Product product);
}