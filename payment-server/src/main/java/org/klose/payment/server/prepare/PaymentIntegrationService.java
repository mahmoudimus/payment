package org.klose.payment.server.prepare;


import org.klose.payment.bo.BillingData;

public interface PaymentIntegrationService {
	/**
	 * 准备订单数据
	 * @param businessNumber 货物标识符
	 * @return
     */
	BillingData prepareBillingData(String businessNumber);


	void saveTransactionId(String businessNumber, Long transactionId);
}
