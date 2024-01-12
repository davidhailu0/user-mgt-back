package com.hajj.hajj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
    public class CorsConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(@NonNull CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT")
                    .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept")
                    .exposedHeaders("JSESSIONID");
        }
    }

