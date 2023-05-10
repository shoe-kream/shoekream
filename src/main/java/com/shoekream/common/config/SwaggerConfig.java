package com.shoekream.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi all() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
                .group("All API of Shoekream")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi user() {
        String[] paths = {"/api/v1/users/**"};

        return GroupedOpenApi.builder()
                .group("User API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi product() {
        String[] paths = {"/api/v1/products/**"};

        return GroupedOpenApi.builder()
                .group("Product API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi brand() {
        String[] paths = {"/api/v1/brands/**"};

        return GroupedOpenApi.builder()
                .group("Brand API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi trade() {
        String[] paths = {"/api/v1/trades/**"};

        return GroupedOpenApi.builder()
                .group("Trade API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi cart() {
        String[] paths = {"/api/v1/carts/**"};

        return GroupedOpenApi.builder()
                .group("Cart API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("ShoeKream App API Test Swagger")
                .description("[Github Repository](https://github.com/shoe-kream/shoekream)");

        // SecuritySecheme명
        String jwtSchemeName = "jwtAuth";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}