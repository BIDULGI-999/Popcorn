package com.bidulgi.paymentservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import com.bidulgi.common.security.CustomAccessDeniedHandler;
import com.bidulgi.common.security.SecurityConfigBase;

@Configuration
public class PaymentSecurityConfig extends SecurityConfigBase {
	public PaymentSecurityConfig(CustomAccessDeniedHandler accessDeniedHandler) {
		super(accessDeniedHandler);
	}
}
