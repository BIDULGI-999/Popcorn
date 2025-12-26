package com.bidulgi.userservice.application.service;

import com.bidulgi.common.auth.GeneratedToken;
import com.bidulgi.common.auth.JwtTokenProvider;
import com.bidulgi.common.globalException.ErrorCode;
import com.bidulgi.common.globalException.custom.AuthorizationException;
import com.bidulgi.common.globalException.custom.EntityNotFoundException;
import com.bidulgi.common.globalException.custom.InternalServiceException;
import com.bidulgi.userservice.application.dto.AuthTokensResponse;
import com.bidulgi.userservice.application.dto.LoginRequest;
import com.bidulgi.userservice.application.dto.RefreshTokenRequest;
import com.bidulgi.userservice.domain.auth.RefreshTokenRepository;
import com.bidulgi.userservice.domain.auth.TokenBlacklistRepository;
import com.bidulgi.userservice.domain.model.User;
import com.bidulgi.userservice.domain.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final TokenBlacklistRepository tokenBlacklistRepository;

	@Override
	public AuthTokensResponse login(LoginRequest request) {
		// 1. 이메일로 유저 조회
		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new AuthorizationException(ErrorCode.FORBIDDEN_ACCESS,
				"이메일 또는 비밀번호가 올바르지 않습니다."));

		// 2. 비밀번호 검증
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new AuthorizationException(ErrorCode.FORBIDDEN_ACCESS,
				"이메일 또는 비밀번호가 올바르지 않습니다.");
		}

		// 3. access / refresh 토큰 발급
		GeneratedToken token = jwtTokenProvider.generateTokens(
			user.getId(),
			user.getRole().getKey()
		);

		// 4. 기존 refresh 토큰 삭제 후 새로 저장 (rotation 대비)
		try {
			refreshTokenRepository.deleteAllByUserId(user.getId());
			refreshTokenRepository.save(
				user.getId(),
				token.refreshToken(),
				token.refreshTokenExpiresAt()
			);
		} catch (Exception e) {
			throw new InternalServiceException(
				ErrorCode.INTERNAL_SERVER_ERROR,
				"Refresh 토큰 저장 중 오류가 발생했습니다."
			);
		}

		return AuthTokensResponse.of(
			token.accessToken(),
			token.refreshToken()
		);
	}

	@Override
	public AuthTokensResponse refresh(RefreshTokenRequest request) {
		String refreshToken = request.refreshToken();

		// 1. 토큰 파싱 및 타입 검증
		Jws<Claims> claims;
		try {
			claims = jwtTokenProvider.parse(refreshToken);
		} catch (Exception e) {
			throw new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"유효하지 않은 refresh 토큰입니다."
			);
		}

		String type = (String)claims.getBody().get("type");
		if (!"REFRESH".equals(type)) {
			throw new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"Refresh 토큰이 아닙니다."
			);
		}

		UUID userId;
		try {
			userId = UUID.fromString(claims.getBody().getSubject());
		} catch (Exception e) {
			throw new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"유효하지 않은 refresh 토큰입니다."
			);
		}

		// 2. 서버에 저장된 refresh 토큰인지 확인
		refreshTokenRepository.findValidToken(userId, refreshToken)
			.orElseThrow(() -> new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"유효하지 않은 refresh 토큰입니다."
			));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(
				ErrorCode.RESOURCE_NOT_FOUND,
				"유저를 찾을 수 없습니다. id=" + userId));
		// 3. 새 access / refresh 발급
		GeneratedToken newToken = jwtTokenProvider.generateTokens(
			userId,
			user.getRole().getKey()
		);

		// 4. 기존 refresh 삭제 + 새 refresh 저장 (rotation)
		try {
			refreshTokenRepository.delete(userId, refreshToken);
			refreshTokenRepository.save(
				userId,
				newToken.refreshToken(),
				newToken.refreshTokenExpiresAt()
			);
		} catch (Exception e) {
			throw new InternalServiceException(
				ErrorCode.INTERNAL_SERVER_ERROR,
				"Refresh 토큰 로테이션 처리 중 오류가 발생했습니다."
			);
		}

		return AuthTokensResponse.of(newToken.accessToken(), newToken.refreshToken());
	}

	@Override
	public void logout(String accessToken) {

		Jws<Claims> jws;
		try {
			jws = jwtTokenProvider.parse(accessToken);
		} catch (Exception e) {
			throw new AuthorizationException(ErrorCode.FORBIDDEN_ACCESS,
				"유효하지 않은 access 토큰입니다."
			);
		}

		Claims body = jws.getPayload();

		String type = body.get("type", String.class);
		if (!"ACCESS".equals(type)) {
			throw new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"Access 토큰이 아닙니다."
			);
		}

		String jti = body.getId();
		Instant expiresAt = body.getExpiration().toInstant();
		UUID userId;
		try {
			userId = UUID.fromString(body.getSubject());
		} catch (Exception e) {
			throw new AuthorizationException(
				ErrorCode.FORBIDDEN_ACCESS,
				"유효하지 않은 access 토큰입니다."
			);
		}
		try {
			tokenBlacklistRepository.blacklist(jti, expiresAt);
			refreshTokenRepository.deleteAllByUserId(userId);
		} catch (Exception e) {
			throw new InternalServiceException(
				ErrorCode.INTERNAL_SERVER_ERROR,
				"로그아웃 처리 중 오류가 발생했습니다."
			);
		}
	}
}
