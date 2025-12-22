package com.bidulgi.paymentservice.application.port.out;

import java.util.UUID;

public interface PaymentEventPublisher {

	void publishPaymentSucceeded(String reservationId, UUID paymentId, int paidAmount);

	void publishPaymentCanceled(UUID paymentId, int balanceAmount);
}
