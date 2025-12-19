package com.bidulgi.paymentservice.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bidulgi.paymentservice.application.dto.ApplyCancelCommand;
import com.bidulgi.paymentservice.application.port.out.PaymentGateway;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelResult;
import com.bidulgi.paymentservice.domain.model.CompensationLog;
import com.bidulgi.paymentservice.domain.model.Payment;
import com.bidulgi.paymentservice.application.port.out.PaymentEventPublisher;
import com.bidulgi.paymentservice.domain.repository.CompensationLogRepository;
import com.bidulgi.paymentservice.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCompensationService {

	private static final String COMPENSATION_REASON = "예약 처리 실패로 인한 자동 환불";

	private final PaymentRepository paymentRepository;
	private final PaymentService paymentService;
	private final PaymentGateway paymentGateway;
	private final PaymentEventPublisher paymentEventPublisher;
	private final CompensationLogRepository compensationLogRepository;

	// 1단계: CompensationLog 생성 (별도 트랜잭션)
	@Transactional
	public CompensationLog initCompensation(UUID paymentId) {
		// 이미 존재하면 기존 로그 반환 (재시도 지원)
		return compensationLogRepository.findByPaymentId(paymentId)
			.orElseGet(() -> {
				CompensationLog log = CompensationLog.create(paymentId, COMPENSATION_REASON);
				return compensationLogRepository.save(log);
			});
	}

	// 2단계: 결제 취소 API 호출 (트랜잭션 없음)
	public PaymentCancelResult cancelPayment(Payment payment) {
		PaymentCancelRequest request = new PaymentCancelRequest(
			COMPENSATION_REASON,
			payment.getBalanceAmount()
		);
		return paymentGateway.cancel(request, payment.getPaymentKey());
	}

	// 3단계: TOSS_CANCELED 상태 저장 (별도 트랜잭션)
	@Transactional
	public void markTossCanceledState(UUID paymentId) {
		CompensationLog compensationLog = compensationLogRepository.findByPaymentId(paymentId)
			.orElseThrow(() -> new IllegalStateException("CompensationLog not found: " + paymentId));
		compensationLog.markTossCanceled();
		compensationLogRepository.save(compensationLog);
	}

	// 4단계: DB 업데이트 + 완료 처리 (별도 트랜잭션)
	@Transactional
	public void completeCompensation(UUID paymentId, PaymentCancelResult cancelResult) {
		ApplyCancelCommand command = ApplyCancelCommand.from(cancelResult);
		Payment cancelledPayment = paymentService.cancelPayment(paymentId, command);

		CompensationLog compensationLog = compensationLogRepository.findByPaymentId(paymentId)
			.orElseThrow(() -> new IllegalStateException("CompensationLog not found: " + paymentId));

		paymentEventPublisher.publishPaymentCanceled(
			cancelledPayment.getId(),
			cancelledPayment.getBalanceAmount()
		);

		compensationLog.complete();
		compensationLogRepository.save(compensationLog);
	}

	public void compensatePayment(UUID paymentId) {

		CompensationLog compensationLog = initCompensation(paymentId);

		if (compensationLog.isCompleted()) {
			log.info("이미 완료된 보상. 스킵. paymentId={}", paymentId);
			return;
		}

		Payment payment = paymentRepository.findById(paymentId).orElse(null);
		if (payment == null) {
			log.warn("결제 정보 없음. 보상 스킵. paymentId={}", paymentId);
			return;
		}
		if (!payment.canCancel()) {
			log.warn("취소 불가능한 상태. 보상 스킵. paymentId={}, status={}", paymentId, payment.getStatus());
			return;
		}

		try {
			PaymentCancelResult cancelResult;

			if (compensationLog.isProcessing()) {
				// PROCESSING: 먼저 결제 상태 조회 후 취소 여부 결정
				PaymentCancelResult currentState = paymentGateway.getPayment(payment.getPaymentKey());

				if (isAlreadyCanceled(currentState.status())) {
					log.info("이미 취소된 결제. 취소 API 스킵. paymentId={}", paymentId);
					cancelResult = currentState;
				} else {
					cancelResult = cancelPayment(payment);
				}
				markTossCanceledState(paymentId);

			} else if (compensationLog.isTossCanceled()) {
				// TOSS_CANCELED: 결제 상태 조회만
				cancelResult = paymentGateway.getPayment(payment.getPaymentKey());
			} else {
				log.warn("예상치 못한 상태. paymentId={}, status={}", paymentId, compensationLog);
				return;
			}

			// DB 업데이트 + COMPLETED
			completeCompensation(paymentId, cancelResult);

		} catch (Exception e) {
			markFailed(paymentId);
			throw e;
		}
	}

	@Transactional
	public void markFailed(UUID paymentId) {
		compensationLogRepository.findByPaymentId(paymentId)
			.ifPresent(log -> {
				log.fail();
				compensationLogRepository.save(log);
			});
	}

	private boolean isAlreadyCanceled(String status) {
		return "CANCELED".equals(status) || "PARTIAL_CANCELED".equals(status);
	}
}
