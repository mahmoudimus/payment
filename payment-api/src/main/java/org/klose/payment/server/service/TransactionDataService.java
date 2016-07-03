package org.klose.payment.server.service;


import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.PaymentResult;

public interface TransactionDataService {

	public PaymentResult findPaymentByOrderNo(String orderNo);
	
	public PaymentResult findPaymentByTransactionId(Long transId);
	
	public Long createTransactionFromBillingData(BillingData bill);
	
	public String processReturnURL(Long transId, String returnURL);
	
	public PaymentResult findSuccessfulPaymentByBizNo(String bizNo);
	
	public void updatePaymentResult(
			Long transId, boolean isSuccess, String payId, String notifyMsg);
}
