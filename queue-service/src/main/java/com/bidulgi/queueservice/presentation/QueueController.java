package com.bidulgi.queueservice.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.queueservice.application.QueueService;
import com.bidulgi.queueservice.presentation.dto.response.EnqueueResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/queues")
@RequiredArgsConstructor
public class QueueController {

	private  final QueueService queueService;

	// TODO 유저 개발 완료 시 헤더에서 userId 받아오도록 수정 및 UUID로 변경
	@PostMapping
	public Mono<ApiResponse<EnqueueResponse>> enqueue(@RequestParam String userId, @RequestParam String productId) {
		return queueService.enqueue(userId, productId)
			.map(result -> ApiResponse.success(EnqueueResponse.from(result), "대기열 등록 완료"));
	}


}
