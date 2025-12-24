package com.bidulgi.productservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bidulgi.productservice.infrastructure.client.dto.UserProfileResponse;

@FeignClient(
	name = "user-service",           // 유레카 등록 이름 (spring.application.name)
	path = "/internal/v1/users"      // 위에서 만든 internal 컨트롤러 prefix
)
public interface UserClient {

	@GetMapping("/{userId}/profile")
	UserProfileResponse getUserProfile(@PathVariable("userId") UUID userId);
}
