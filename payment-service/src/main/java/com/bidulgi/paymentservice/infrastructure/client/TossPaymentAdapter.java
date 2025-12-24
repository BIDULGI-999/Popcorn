package com.bidulgi.paymentservice.infrastructure.client;

import org.springframework.stereotype.Component;

import com.bidulgi.paymentservice.application.port.out.PaymentGateway;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelResult;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmResult;
import com.bidulgi.paymentservice.infrastructure.client.dto.CancelTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossRequest;
import com.bidulgi.paymentservice.infrastructure.client.dto.ConfirmTossResponse;
import com.bidulgi.paymentservice.infrastructure.client.dto.TossPaymentResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossPaymentAdapter implements PaymentGateway {

	private final TossPaymentClient tossPaymentClient;

	@Override
	public PaymentConfirmResult confirm(PaymentConfirmRequest request) {
		ConfirmTossRequest tossRequest = new ConfirmTossRequest(
			request.paymentKey(),
			request.orderId(),
			request.amount()
		);

		ConfirmTossResponse response = tossPaymentClient.confirm(tossRequest);

		return new PaymentConfirmResult(
			response.paymentKey(),
			response.orderId(),
			response.status(),
			response.method(),
			response.totalAmount(),
			response.balanceAmount(),
			response.approvedAt(),
			response.isPartialCancelable()
		);
	}

	@Override
	public PaymentCancelResult cancel(PaymentCancelRequest request, String paymentKey) {
		CancelTossRequest tossRequest = new CancelTossRequest(
			request.cancelReason(),
			request.cancelAmount()
		);

		TossPaymentResponse response = tossPaymentClient.cancel(tossRequest, paymentKey);

		return toPaymentCancelResult(response);
	}

	@Override
	public PaymentCancelResult getPayment(String paymentKey) {
		TossPaymentResponse response = tossPaymentClient.getPayment(paymentKey);

		return toPaymentCancelResult(response);
	}

	private PaymentCancelResult toPaymentCancelResult(TossPaymentResponse response) {
		return new PaymentCancelResult(
			response.paymentKey(),
			response.orderId(),
			response.status(),
			response.balanceAmount(),
			response.isPartialCancelable(),
			response.cancels() != null
				? response.cancels().stream()
					.map(c -> new PaymentCancelResult.CancelDetail(
						c.transactionKey(),
						c.cancelReason(),
						c.cancelAmount(),
						c.refundableAmount(),
						c.cancelStatus(),
						c.canceledAt()
					))
					.toList()
				: null
		);
	}
}
