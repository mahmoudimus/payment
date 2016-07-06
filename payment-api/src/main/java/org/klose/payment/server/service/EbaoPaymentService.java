package org.klose.payment.server.service;


import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.PaymentForm;

public interface EbaoPaymentService {

	PaymentForm generatePaymentData(BillingData bill) throws Exception;
	
}
