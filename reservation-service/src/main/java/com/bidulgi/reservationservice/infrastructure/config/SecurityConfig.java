package com.bidulgi.reservationservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.bidulgi.common.security.CustomAccessDeniedHandler;
import com.bidulgi.common.security.SecurityConfigBase;

@Configuration
@EnableMethodSecurity
public class SecurityConfig extends SecurityConfigBase {
	public SecurityConfig(CustomAccessDeniedHandler accessDeniedHandler) {
		super(accessDeniedHandler);
	}
}
