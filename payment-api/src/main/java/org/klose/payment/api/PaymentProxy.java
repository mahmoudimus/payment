package org.klose.payment.api;


import org.klose.payment.bo.AccountInfo;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.ForwardViewData;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.constant.PaymentType;

import java.util.List;

public interface PaymentProxy {

	public ForwardViewData createPayment(BillingData bill) throws Exception;
	
	public PaymentResult queryPayment(Long transactionId);
	
	public List<AccountInfo> getAccountsByPaymentType(PaymentType type);
	
	public AccountInfo getAccountbyNo(String accountNo);
	
	public void updatePaymentResult(
			Long transactionId, boolean isSuccess, String payId, String notifyMsg);
}
