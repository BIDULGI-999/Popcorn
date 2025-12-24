package com.bidulgi.productservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.bidulgi.common.security.CustomAccessDeniedHandler;
import com.bidulgi.common.security.SecurityConfigBase;

@Configuration
public class SecurityConfig extends SecurityConfigBase {

	public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler) {
		super(accessDeniedHandler);
	}

	@Override
	protected void configureAuthorization(
		AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
	) {
		// ✅ 상품 조회(공개)
		auth.requestMatchers(HttpMethod.GET, "/v1/products/**").permitAll();

		// ✅ (프론트 호출이면 거의 필수) CORS preflight
		auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

		// 필요 시 추가 공개 경로
		// auth.requestMatchers("/v1/products/search/**").permitAll();
	}
}
