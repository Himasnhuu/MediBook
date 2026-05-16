package com.medibook.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("MediBook API Gateway")
                .version("1.0.0")
                .description(
                    "Central API documentation for all MediBook microservices. " +
                    "Use the dropdown at the top right to switch between services.")
                .contact(new Contact()
                    .name("MediBook Team")
                    .email("admin@medibook.com")));
    }
}