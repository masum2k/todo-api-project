package com.example.todoapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo App API - Stajyer Projesi (Sidar Orman)")
                        .version("v1.0")
                        .description("Turkcell Stajyer projesi için geliştirilen Todo API dokümantasyonu.")
                );
    }
}