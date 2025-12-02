package com.bidulgi.userservice.config;

import com.bidulgi.common.security.CustomAccessDeniedHandler;
import com.bidulgi.common.security.SecurityConfigBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSecurityConfig extends SecurityConfigBase {

	public UserSecurityConfig(CustomAccessDeniedHandler accessDeniedHandler) {
		super(accessDeniedHandler);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configureAuthorization(
		AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth
	) {
		// 회원가입 / 로그인은 공개
		auth.requestMatchers(HttpMethod.POST, "/api/users", "/api/users/login")
			.permitAll();

		// 조회/수정은 로그인 필요
		auth.requestMatchers(HttpMethod.GET, "/api/users/**").authenticated();
		auth.requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated();

		// 삭제는 Admin
		auth.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN");
	}
}
