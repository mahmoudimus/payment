package org.klose.payment.api;


import org.klose.payment.bo.PaymentResult;

public interface CallBackAgent {
	void processPaymentCallback(PaymentResult payResult);
}
