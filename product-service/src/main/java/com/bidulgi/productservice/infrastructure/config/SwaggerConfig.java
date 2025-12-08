package com.bidulgi.productservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
