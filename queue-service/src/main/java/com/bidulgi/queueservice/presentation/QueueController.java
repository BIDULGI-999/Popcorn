package com.bidulgi.queueservice.presentation;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bidulgi.common.response.ApiResponse;
import com.bidulgi.common.security.UserPrincipal;
import com.bidulgi.queueservice.application.QueueService;
import com.bidulgi.queueservice.presentation.dto.response.EnqueueResponse;
import com.bidulgi.queueservice.presentation.dto.response.PositionResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/queues")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class QueueController {

	private  final QueueService queueService;

	/**
	 * POST /v1/queues/{productId}
	 * 대기열 등록
	 */
	@PostMapping("/{productId}")
	public Mono<ApiResponse<EnqueueResponse>> enqueue(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable UUID productId
	) {
		return queueService.enqueue(userPrincipal.id(), productId)
			.map(result -> ApiResponse.success(EnqueueResponse.from(result)));
	}

	/**
	 * GET /v1/queues/position/{productId}
	 * 대기열 위치 스트리밍
	 */
	@GetMapping(value = "/position/{productId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<PositionResponse>> streamPosition(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable UUID productId
	) {
		return queueService.subscribePosition(userPrincipal.id(), productId)
			.map(result -> ServerSentEvent.<PositionResponse>builder()
				.event("position")
				.data(PositionResponse.from(result))
				.build()
			);
	}
}
