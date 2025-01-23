# Products Service

## Overview

The **Products Service** is a cloud-based microservice designed to manage the products offered in an e-commerce platform. This service allows administrators to create, update, and delete products, while customers can search for and retrieve product details. Built with scalability, performance, and reliability in mind, the service leverages AWS technologies such as DynamoDB, SNS, SQS, ECS Fargate, and API Gateway to ensure a seamless user experience.

---

### **[Ecommerce](https://github.com/dobrevd/EcommerceECS_CDK_aws)**

This project is an e-commerce platform built using a microservices architecture. The system consists of three main microservices: [ProductsService](https://github.com/dobrevd/productservice_aws), [AuditService](https://github.com/dobrevd/auditservice_aws), and [InvoicesService](https://github.com/dobrevd/invoiceservice_aws). This project demonstrates an infrastructure-as-code (IaC) solution for deploying a scalable e-commerce application using the AWS Cloud Development Kit (CDK) with Java. Each microservice is designed to handle specific aspects of the platform, ensuring modularity, scalability, and maintainability.

### **[AuditService](https://github.com/dobrevd/auditservice_aws)**

This is a cloud-based microservice designed to manage the products offered in an e-commerce platform. This service allows administrators to create, update, and delete products, while customers can search for and retrieve product details. Built with scalability, performance, and reliability in mind, the service leverages AWS technologies such as DynamoDB, SNS, SQS, ECS Fargate, and API Gateway to ensure a seamless user experience.
### **[InvoicesService](https://github.com/dobrevd/invoiceservice_aws)**

This is a cloud-based microservice designed to handle the import, processing, and management of invoice files for an e-commerce backend system. This service integrates with a legacy system to enable efficient storage and retrieval of invoices, leveraging AWS technologies such as DynamoDB, S3, SQS, and ECS Fargate for scalability, reliability, and performance.

---

## Features

1. **Product Management**:
    - Create, update, and delete products through API requests.
    - Fetch product details for customers based on search queries.

2. **Event Notification**:
    - Publishes product-related events (e.g., product creation, updates) to an SNS topic (`product-events`).

3. **Data Persistence**:
    - Stores product data in a DynamoDB table (`products`).
    - Utilizes a Global Secondary Index (GSI) for efficient searching by product code.

4. **Scalability and Monitoring**:
    - Deployed using AWS Fargate for seamless scaling.
    - Auto-scaling for DynamoDB tables and Fargate tasks to handle varying loads.
    - Logs and metrics collected with AWS X-Ray and CloudWatch for monitoring and debugging.

5. **Alerting**:
    - Triggers alarms on code duplication events using CloudWatch and SNS for notifications.

---

## Architecture

The **Products Service** is built using a robust and modular architecture:

1. **API Gateway**:
    - Acts as the entry point for all incoming requests, enabling secure and scalable communication with the service.

2. **AWS Fargate**:
    - Hosts the Spring Boot application, ensuring seamless deployment and scaling of the service.

3. **DynamoDB**:
    - Primary database for storing product information.
    - Features a GSI for efficient lookups by product code.

4. **SNS and SQS**:
    - Publishes product-related events to an SNS topic.
    - SQS queues consume the events for downstream services like the **Audit Service**.

5. **CloudWatch**:
    - Monitors service metrics and logs.
    - Alerts administrators of critical issues (e.g., code duplication).

6. **X-Ray**:
    - Provides end-to-end tracing for debugging and performance optimization.

---

## Technologies Used

- **Java** (Core Application Logic)
- **Spring Boot** (Backend Framework)
- **AWS CDK** (Infrastructure as Code)
- **AWS Services**:
   - DynamoDB (Database)
   - SNS (Simple Notification Service)
   - SQS (Event Queue)
   - ECS Fargate (Containerized Service)
   - CloudWatch and X-Ray (Monitoring)