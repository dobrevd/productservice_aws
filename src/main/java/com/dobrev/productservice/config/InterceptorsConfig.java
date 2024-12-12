package com.dobrev.productservice.config;

import com.dobrev.productservice.products.interceptor.ProductsInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorsConfig implements WebMvcConfigurer {
    private final ProductsInterceptor productsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.productsInterceptor)
                .addPathPatterns("api/products/**");
    }
}