package com.bidulgi.productservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @io.swagger.v3.oas.annotations.info.Info(
		title = "PopUp Product Service API",
		version = "v1",
		description = "PopUp 플랫폼 상품 서비스 API 문서"
	),
	servers = {
		@Server(url = "http://localhost:8300", description = "Local Server")
	},
	security = {
		@SecurityRequirement(name = "bearerAuth") // 기본적으로 전역에 보안 요구
	}
)
@SecurityScheme(
	name = "bearerAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "bearer",
	bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("팝업스토어/상품 서비스 API")
                        .description("상품 등록, 회차 관리, 예약 슬롯 생성 및 조회 API 명세서")
                        .version("v1.0.0"));
    }
}
