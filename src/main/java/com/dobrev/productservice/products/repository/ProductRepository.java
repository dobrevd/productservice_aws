package com.dobrev.productservice.products.repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.dobrev.productservice.products.exceptions.ProductError;
import com.dobrev.productservice.products.exceptions.ProductException;
import com.dobrev.productservice.products.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
@XRayEnabled
@Slf4j
public class ProductRepository {
    @Value("${products.page_size}")
    private Integer pageSize;

    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;
    private final DynamoDbAsyncTable<Product> productsTable;

    public ProductRepository(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                             @Value("${aws.productsddb.name}") String productsDdbName) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.productsTable = dynamoDbEnhancedAsyncClient.table(productsDdbName, TableSchema.fromBean(Product.class));
    }

    public CompletableFuture<Product> getByCode(String code){
        return checkIfCodeExists(code)
                .thenCompose(product ->
                        product != null ? getById(product.getId()) : CompletableFuture.completedFuture(null));
    }

    public CompletableFuture<Product> checkIfCodeExists(String code){
        List<Product> products = new ArrayList<>();

        var request = QueryEnhancedRequest.builder()
                .limit(1)
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(code).build()))
                .build();
        productsTable.index("codeIdx")
                .query(request)
                .subscribe(productPage -> products.addAll(productPage.items()));

        return CompletableFuture.supplyAsync(() -> products.stream().findFirst().orElse(null));
    }

    public PagePublisher<Product> getAll(){
        return productsTable.scan(ScanEnhancedRequest.builder()
                .limit(pageSize)
                .build());
    }

    public CompletableFuture<Product> getById(String productId){
        log.info("Product id: {}", productId);
        return productsTable.getItem(Key.builder()
                        .partitionValue(productId)
                .build());
    }

    public CompletableFuture<Void> create(Product product){
        var productWithTheSameCode = checkIfCodeExists(product.getCode()).join();
        if (productWithTheSameCode != null){
            log.error("Can not create a product with same code");
            throw new ProductException(ProductError.PRODUCT_CODE_ALREADY_EXISTS, productWithTheSameCode.getId());
        }
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