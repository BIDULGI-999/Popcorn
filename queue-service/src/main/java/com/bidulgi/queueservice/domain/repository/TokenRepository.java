package com.bidulgi.queueservice.domain.repository;

import reactor.core.publisher.Mono;

public interface TokenRepository {

	/**
	 * 토큰 조회
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 조회된 토큰
	 */
	Mono<String> findToken(String userId, String productId);

	/**
	 * 토큰 저장
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @param token 토큰
	 * @return 저장 성공 여부
	 */
	Mono<Boolean> saveToken(String userId, String productId, String token);
}
