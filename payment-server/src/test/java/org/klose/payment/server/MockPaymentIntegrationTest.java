package org.klose.payment.server;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.constant.PaymentConstant;
import org.klose.payment.server.service.PaymentIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-payment-beans.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class MockPaymentIntegrationTest {

    @Resource(name = "mockPaymentIntegtationService")
    private PaymentIntegrationService service;


    private final static Logger logger = LoggerFactory
            .getLogger(MockPaymentIntegrationTest.class);


    @Test
    public void createBillingData() throws Exception {
        String bizNo = UUID.randomUUID().toString();
        BillingData data = service.prepareBillingData(bizNo);
        Assert.assertNotNull(data);
        logger.info("prepared billing data : {}", data);

        Assert.assertEquals(BigDecimal.ONE, data.getPrice());
        Assert.assertEquals("test payment", data.getDescription());
        Assert.assertEquals("test payment", data.getSubject());
        Assert.assertEquals(bizNo, data.getBizNo());
        Assert.assertEquals(1, data.getQuantity());
        Assert.assertEquals(PaymentConstant.CURRENCY_CNY, data.getCurrency());

    }

}
