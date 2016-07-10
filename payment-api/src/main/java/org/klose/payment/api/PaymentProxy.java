package org.klose.payment.api;


import org.klose.payment.bo.PaymentForm;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.bo.AccountInfo;
import org.klose.payment.bo.BillingData;

public interface PaymentProxy {

	PaymentForm createPayment(BillingData bill) throws Exception;
	
	PaymentResult queryPayment(Long transactionId);

	AccountInfo getAccountbyNo(String accountNo);
	
	void updatePaymentResult(
			Long transactionId, boolean isSuccess, String payId, String notifyMsg);

	String findReturnUrl(Long transactionId);
}
