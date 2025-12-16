package com.bidulgi.queueservice.domain.repository;

import java.util.UUID;

import com.bidulgi.queueservice.domain.vo.QueueState;

import reactor.core.publisher.Mono;

public interface QueueRepository {

	/**
	 * 대기열 등록
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 대기열 상태
	 */
	Mono<QueueState> enqueue(UUID userId, UUID productId);

	/**
	 * 대기열 해제 및 다음 사용자 할당
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 다음 사용자의 유저 아이디와 상품 아이디
	 */
	Mono<String> dequeue(UUID userId, UUID productId);

	/**
	 * 대기열 순번 조회
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 대기열 순번
	 */
	Mono<Long> getPosition(UUID userId, UUID productId);

	/**
	 * 대기열 제거
	 * @param userId 유저 아이디
	 * @param productId 상품 아이디
	 * @return 제거 성공 여부
	 */
	Mono<Boolean> remove(UUID userId, UUID productId);
}
