package com.bidulgi.userservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "PopUp User Service API",
		version = "v1",
		description = "PopUp 플랫폼 유저 서비스 API 문서"
	),
	servers = {
		@Server(url = "http://localhost:8600", description = "Local Server")
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
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new io.swagger.v3.oas.models.info.Info()
				.title("PopUp User Service API")
				.version("v1")
				.description("PopUp 플랫폼 유저 서비스 API 문서")
				.contact(new Contact().name("Bidulgi Team")));
	}
}
