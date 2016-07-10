package org.klose.payment.service.callback.impl;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.api.CallBackAgent;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.constant.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("mockCallbackService")
public class MockCallbackServiceImpl implements CallBackAgent {


    private final static Logger logger = LoggerFactory
            .getLogger(MockCallbackServiceImpl.class);

    @Override
    public void processPaymentCallback(PaymentResult payResult) {
        Assert.isNotNull(payResult, "payment result is empty");
        PaymentStatus paymentStatus = payResult.getStatus();
        Assert.isNotNull(paymentStatus, "payment status canot be null");
        Long transactionId = payResult.getTransactionId();
        Assert.isNotNull(transactionId, "the transactionId %s can not be null",
                transactionId);

        logger.info("start process payment callback");
        logger.debug("[payment result : {} ]", payResult);

        //@TODO according to the payment status, something like issue policy should be invoked
        // in the mock case, just log the payment status
        if (PaymentStatus.Success.equals(paymentStatus))
            logger.info("succesfully finished payment");
        else if (PaymentStatus.Failed.equals(paymentStatus))
            logger.error("errors occurs on payment");
        else
            throw new RuntimeException(
                    "not allowed payment result status");


    }

}
