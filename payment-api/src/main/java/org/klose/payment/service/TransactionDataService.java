package org.klose.payment.service;


import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentResult;

public interface TransactionDataService {

    PaymentResult findPaymentByOrderNo(String orderNo);

    PaymentResult findPaymentByTransactionId(Long transId);

    Long createTransactionFromBillingData(BillingData bill);

    String processReturnURL(Long transId, String returnURL);

    PaymentResult findSuccessfulPaymentByBizNo(String bizNo);

    void updatePaymentResult(
            Long transId, boolean isSuccess, String payId, String notifyMsg);

    String findReturnURL(Long transactonId);
}
