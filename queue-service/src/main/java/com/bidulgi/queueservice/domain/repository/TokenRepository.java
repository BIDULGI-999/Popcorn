package com.bidulgi.queueservice.domain.repository;

import java.util.UUID;

import reactor.core.publisher.Mono;

public interface TokenRepository {

	/**
	 * 토큰 조회
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 조회된 토큰
	 */
	Mono<String> findToken(UUID userId, UUID productId);

	/**
	 * 토큰 저장
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @param token 토큰
	 * @return 저장 성공 여부
	 */
	Mono<Boolean> saveToken(UUID userId, UUID productId, String token);
}
