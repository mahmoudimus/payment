package org.klose.payment.api;


import org.klose.payment.bo.PaymentForm;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.bo.AccountInfo;
import org.klose.payment.bo.BillingData;

import java.util.Map;

/**
 * payment proxy
 */
public interface PaymentProxy {

	PaymentForm createPayment(BillingData bill);
	
	PaymentResult queryPayment(Long transactionId);

	AccountInfo getAccountbyNo(String accountNo);
	
	void updatePaymentResult(
			Long transactionId, boolean isSuccess, String payId, String notifyMsg);

	String findReturnUrl(Long transactionId);

	Map<String, Object> parseConfig(String accountNo);

}
