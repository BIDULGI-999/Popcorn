package com.bidulgi.common.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.bidulgi.common.security.UserPrincipal;

@EnableJpaAuditing
@Configuration
public class JpaAuditConfig {

	@Bean
	public AuditorAware<UUID> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			if (authentication == null || !authentication.isAuthenticated()) {
				return Optional.empty();
			}

			Object principal = authentication.getPrincipal();

			if (principal instanceof UserPrincipal userPrincipal) {
				return Optional.of(userPrincipal.id());
			}

			return Optional.empty();
		};
	}
}