package org.klose.payment.server.api;


import org.klose.payment.server.bo.PaymentResult;

public interface CallBackAgent {
	void processPaymentCallback(PaymentResult payResult);
}
