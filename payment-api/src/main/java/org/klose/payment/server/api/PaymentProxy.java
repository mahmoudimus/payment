package org.klose.payment.server.api;


import org.klose.payment.server.bo.AccountInfo;
import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.PaymentForm;
import org.klose.payment.server.bo.PaymentResult;
import org.klose.payment.server.constant.PaymentType;

import java.util.List;

public interface PaymentProxy {

	public PaymentForm createPayment(BillingData bill) throws Exception;
	
	public PaymentResult queryPayment(Long transactionId);
	
	public List<AccountInfo> getAccountsByPaymentType(PaymentType type);
	
	public AccountInfo getAccountbyNo(String accountNo);
	
	public void updatePaymentResult(
			Long transactionId, boolean isSuccess, String payId, String notifyMsg);
}
