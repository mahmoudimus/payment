package org.klose.payment.server.service;


import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.PaymentResult;

public interface TransactionDataService {

    PaymentResult findPaymentByOrderNo(String orderNo);

    PaymentResult findPaymentByTransactionId(Long transId);

    Long createTransactionFromBillingData(BillingData bill);

    String processReturnURL(Long transId, String returnURL);

    PaymentResult findSuccessfulPaymentByBizNo(String bizNo);

    public void updatePaymentResult(
            Long transId, boolean isSuccess, String payId, String notifyMsg);
}
