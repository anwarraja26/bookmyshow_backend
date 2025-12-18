package com.example.moviebooking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        final String[] allowedOrigins = resolveAllowedOrigins();
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins)
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Value("${app.frontend.origin:http://localhost:5173}")
    private String frontendOrigin;

    private String[] resolveAllowedOrigins() {
        if (frontendOrigin == null || frontendOrigin.isBlank()) {
            return new String[] {"http://localhost:5173"};
        }
        return frontendOrigin.split(",");
    }
}
