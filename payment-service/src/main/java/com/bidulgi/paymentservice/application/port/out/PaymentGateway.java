package com.bidulgi.paymentservice.application.port.out;

import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentCancelResult;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmRequest;
import com.bidulgi.paymentservice.application.port.out.dto.PaymentConfirmResult;

public interface PaymentGateway {

	PaymentConfirmResult confirm(PaymentConfirmRequest request);

	PaymentCancelResult cancel(PaymentCancelRequest request, String paymentKey);

	PaymentCancelResult getPayment(String paymentKey);
}
