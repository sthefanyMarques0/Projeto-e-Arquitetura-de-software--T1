package com.bcopstein.voos.config;

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
                        .title("Sistema de Gestão e Validação de Planos de Voo")
                        .version("1.0.0")
                        .description("API REST para criação, validação, aprovação e monitoramento de planos de voo e ocupação de recursos da malha aérea (Trabalho Final PAS)."));
    }
}
