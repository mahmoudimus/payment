package org.klose.payment.server.prepare.impl;


import org.klose.payment.bo.BillingData;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.server.prepare.PaymentIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service("mockPaymentIntegtationService")
public class MockPaymentIntegrationServiceImpl implements
        PaymentIntegrationService {

    private Logger logger = LoggerFactory
            .getLogger(MockPaymentIntegrationServiceImpl.class);

    @Override
    public BillingData prepareBillingData(String businessNumber) {
        Assert.isNotNull(businessNumber, "business number can not be blank");
        logger.info("start prepare mock billing data");
        logger.debug("[businessNumber = {}]", businessNumber);

        BillingData result = new BillingData();

        result.setSubject("test payment");
        result.setDescription("test payment");
        result.setBizNo(businessNumber);
        result.setPrice(BigDecimal.valueOf(0.01));
        result.setCurrency(PaymentConstant.CURRENCY_CNY);
        result.setQuantity(1);
        result.setBusinessEffectiveDate(new Date());

        prepareExtensionData(result);
        logger.info("finish prepare mock billing data");
        logger.debug("[prepared billing data : {}]", result);

        return result;
    }


    /**
     * override for extension
     *
     * @param data: order data
     */
    protected void prepareExtensionData(BillingData data) {
    }

    @Override
    public void saveTransactionId(String businessNumber, Long transactionId) {
        //@ TODO the tranactionId should be saved in db with the order of commodity
        logger.info("start saving transactionId in order table");
        logger.debug("[businessNumber = {}, transactionId = {}]"
                , businessNumber, transactionId);
//		agreementService.persistPolicyWithTransactionId(businessNumber,
//				transactionId);

    }

}
