package com.bidulgi.userservice.presentation;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.userservice.application.dto.*;
import com.bidulgi.userservice.application.service.AuthService;
import lombok.RequiredArgsConstructor;
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
	public ApiResponse<Void> logout(
		@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
		@RequestBody(required = false) LogoutRequest request
	) {
		String accessToken = extractBearerToken(authorizationHeader);
		String refreshToken = request != null ? request.refreshToken() : null;

		authService.logout(accessToken, refreshToken);
		return ApiResponse.success("success");
	}

	private String extractBearerToken(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization 헤더 형식이 올바르지 않습니다.");
		}

		return authorizationHeader.substring("Bearer ".length());
	}
}
