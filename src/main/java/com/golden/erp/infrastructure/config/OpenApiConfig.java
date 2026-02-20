package com.golden.erp.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Mini-ERP de Pedidos API")
                        .description("""
                                API REST para gerenciamento de clientes, produtos e pedidos.
                                
                                **Autenticação:** Use o endpoint `/api/auth/login` com as credenciais `admin/admin123` \
                                para obter um token JWT. Em seguida, clique em "Authorize" e insira: `Bearer {seu_token}`
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Golden ERP")
                                .email("contato@golden.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insira o token JWT obtido no endpoint /api/auth/login")));
    }
}
