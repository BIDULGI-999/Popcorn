package com.bidulgi.userservice.presentation;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.userservice.application.dto.*;
import com.bidulgi.userservice.application.service.AuthService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<AuthTokensResponse> login(@RequestBody LoginRequest request) {
		AuthTokensResponse tokens = authService.login(request);
		return ApiResponse.success(tokens, "success");
	}

	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public ApiResponse<AuthTokensResponse> refresh(@RequestBody RefreshTokenRequest request) {
		AuthTokensResponse tokens = authService.refresh(request);
		return ApiResponse.success(tokens, "success");
	}

	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "bearerAuth")
	public ApiResponse<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
		String token = extractBearerToken(authorization);
		authService.logout(token);
		return ApiResponse.success("success");
	}

	private String extractBearerToken(String authorization) {
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
		}

		return authorization.substring("Bearer ".length());
	}
}
