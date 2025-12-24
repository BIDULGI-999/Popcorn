package com.bidulgi.paymentservice.infrastructure.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bidulgi.common.security.UserPrincipal;

@Configuration
public class PaymentAuditConfig {

	// 시스템 작업(Kafka Consumer 등)에서 사용할 고정 UUID
	private static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@Bean
	@Primary
	public AuditorAware<UUID> paymentAuditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.of(SYSTEM_USER_ID);
			}

			Object principal = authentication.getPrincipal();

			if (principal instanceof UserPrincipal userPrincipal) {
				return Optional.of(userPrincipal.id());
			}

			return Optional.of(SYSTEM_USER_ID);
		};
	}
}
