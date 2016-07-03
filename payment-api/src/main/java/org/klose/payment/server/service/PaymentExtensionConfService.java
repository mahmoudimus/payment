package org.klose.payment.server.service;


import org.klose.payment.server.bo.PaymentExtensionConf;

public interface PaymentExtensionConfService {
    PaymentExtensionConf getPaymentExtensionByBizTypeAndAccountType(Integer accountType, String bizType);
}
