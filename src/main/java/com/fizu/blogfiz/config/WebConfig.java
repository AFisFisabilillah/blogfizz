package com.fizu.blogfiz.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("http://localhost:8080", "https://eventfy.com")
                .allowedMethods("POST", "GET", "DELETE", "PUT", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
