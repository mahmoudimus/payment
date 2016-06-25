package org.klose.payment.service;


import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.ForwardViewData;

public interface EbaoPaymentService {

	public ForwardViewData generatePaymentData(BillingData bill) throws Exception;
	
}
