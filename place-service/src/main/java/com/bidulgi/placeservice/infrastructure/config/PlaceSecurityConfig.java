package com.bidulgi.placeservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import com.bidulgi.common.security.CustomAccessDeniedHandler;
import com.bidulgi.common.security.SecurityConfigBase;

@Configuration
public class PlaceSecurityConfig extends SecurityConfigBase {
	public PlaceSecurityConfig(CustomAccessDeniedHandler accessDeniedHandler) {
		super(accessDeniedHandler);
	}
}
