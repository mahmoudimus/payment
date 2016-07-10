package org.klose.payment.service;


import org.klose.payment.bo.PaymentExtensionConf;

public interface PaymentExtensionConfService {
    PaymentExtensionConf getPaymentExtensionByBizTypeAndAccountType(Integer accountType, String bizType);
}
