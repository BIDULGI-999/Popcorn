package com.bidulgi.userservice.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.userservice.application.dto.UserProfileResponse;
import com.bidulgi.userservice.application.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class InternalUserController {

	private final UserService userService;

	/**
	 * 다른 마이크로서비스(예: product-service)에서 호출하는 유저 프로필 조회
	 */
	@GetMapping("/{userId}/profile")
	public UserProfileResponse getUserProfile(@PathVariable UUID userId) {
		return userService.getUserProfile(userId);
	}
}
