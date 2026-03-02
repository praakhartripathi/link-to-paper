package com.webtopdf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // allowedOriginPatterns supports wildcards and works with
                        // any Vite dev port (5173, 5174, etc.) and production domains
                        .allowedOriginPatterns(
                                "http://localhost:*",
                                "https://*.onrender.com",
                                "https://*.vercel.app"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        // Expose headers that JS needs to read for the PDF download
                        .exposedHeaders("Content-Disposition", "Content-Length", "Content-Type")
                        .maxAge(3600);
            }
        };
    }
}

