package com.dobrev.productservice.products.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Getter
@Setter
@Builder
public class Product {
    private String id;
    private String productName;
    private String code;
    private float price;
    private String model;
//    private String url;

    @DynamoDbPartitionKey
    public String getId(String id) {
        return id;
    }
}