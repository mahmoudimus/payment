package org.klose.payment.server.service;


import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.ForwardViewData;

public interface EbaoPaymentService {

	public ForwardViewData generatePaymentData(BillingData bill) throws Exception;
	
}
