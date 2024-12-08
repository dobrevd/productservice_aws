package com.dobrev.productservice.products.repository;

import com.dobrev.productservice.products.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.concurrent.CompletableFuture;

@Repository
public class ProductRepository {
// TODO   @Value("${products.page_size}")
    private Integer pageSize = 100;

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final DynamoDbAsyncTable<Product> productsTable;

    public ProductRepository(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                             @Value("${aws.productsddb.name}") String productsDdbName) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.productsTable = dynamoDbEnhancedAsyncClient.table(productsDdbName, TableSchema.fromBean(Product.class));
    }

    public PagePublisher<Product> getAll(){
        return productsTable.scan(ScanEnhancedRequest.builder()
                .limit(pageSize)
                .build());
    }

    public CompletableFuture<Product> getById(String productId){
        return productsTable.getItem(Key.builder()
                        .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Void> create(Product product){
        return productsTable.putItem(product);
    }

    public  CompletableFuture<Product> deleteById(String productId){
        return productsTable.deleteItem(Key.builder()
                        .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Product> update(Product product, String productId){
        product.setId(productId);
        return productsTable.updateItem(UpdateItemEnhancedRequest.builder(Product.class)
                        .item(product)
                        .conditionExpression(Expression.builder()
                                .expression("attribute_exists(id)")
                                .build())
                .build());
    }
}