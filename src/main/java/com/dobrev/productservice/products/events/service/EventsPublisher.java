package com.dobrev.productservice.products.events.service;

import com.amazonaws.xray.AWSXRay;
import com.dobrev.productservice.products.dto.ProductDto;
import com.dobrev.productservice.products.events.dto.EventType;
import com.dobrev.productservice.products.events.dto.ProductEventDto;
import com.dobrev.productservice.products.events.dto.ProductFailureEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.Topic;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class EventsPublisher {
    private final SnsAsyncClient snsAsyncClient;
    @Qualifier("productEventsTopic")
    private final Topic productEventsTopic;
    private final ObjectMapper objectMapper;

    public EventsPublisher(SnsAsyncClient snsAsyncClient, Topic productEventsTopic, ObjectMapper objectMapper) {
        this.snsAsyncClient = snsAsyncClient;
        this.productEventsTopic = productEventsTopic;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<PublishResponse> sendProductFailureEvent(ProductFailureEventDto productFailureEventDto){
        String data = null;
        try {
            data = objectMapper.writeValueAsString(productFailureEventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sendEvent(data, EventType.PRODUCT_FAILURE);
    }

    public CompletableFuture<PublishResponse> sendProductEvents(ProductEventDto productEventDto,
                                                                EventType eventType,
                                                                String email) {
        String data = null;
        try {
            data = objectMapper.writeValueAsString(productEventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return sendEvent(data, eventType);
    }

    private CompletableFuture<PublishResponse> sendEvent(String data, EventType eventType){
        return this.snsAsyncClient.publish(PublishRequest.builder()
                        .message(data)
                        .messageAttributes(Map.of(
                                "eventType", MessageAttributeValue.builder()
                                                .dataType("String")
                                                .stringValue(eventType.name())
                                        .build(),
                                "requestId", MessageAttributeValue.builder()
                                        .dataType("String")
                                        .stringValue(ThreadContext.get("requestId"))
                                        .build(),
                                "traceId", MessageAttributeValue.builder()
                                        .dataType("String")
                                                .stringValue(Objects.requireNonNull(
                                                        AWSXRay.getCurrentSegment()).getTraceId().toString())
                                        .build()
                        ))
                        .topicArn(this.productEventsTopic.topicArn())
                .build());
    }
}