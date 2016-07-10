package org.klose.payment.service;


import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;

public interface PaymentService {

	PaymentForm generatePaymentData(BillingData bill) throws Exception;
	
}
