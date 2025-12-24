package com.bidulgi.userservice.application.service;

import com.bidulgi.userservice.application.dto.AuthTokensResponse;
import com.bidulgi.userservice.application.dto.LoginRequest;
import com.bidulgi.userservice.application.dto.RefreshTokenRequest;

public interface AuthService {

	AuthTokensResponse login(LoginRequest request);

	AuthTokensResponse refresh(RefreshTokenRequest request);

	void logout(String accessToken);
}
