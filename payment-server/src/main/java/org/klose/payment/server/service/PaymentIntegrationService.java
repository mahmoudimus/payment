package org.klose.payment.server.service;


import org.klose.payment.server.bo.BillingData;

public interface PaymentIntegrationService {
	/**
	 *
	 * @param businessNumber
	 * @return
     */
	BillingData prepareBillingData(String businessNumber);
	
	void saveTransactionId(String businessNumber, Long transactionId);
}
