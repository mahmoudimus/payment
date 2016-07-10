package org.klose.payment.integration.alipay;


import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.constant.PaymentType;
import org.klose.payment.integration.alipay.config.AlipayConstant;
import org.klose.payment.integration.alipay.util.AlipayHelper;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.util.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component(value = "aliGateWayPaymentService")
public class AlipayPaymentService implements PaymentService {
    @Autowired
    private TransactionDataService transactionService;
    @Autowired
    private AccountService accountService;

    private final static Logger logger = LoggerFactory
            .getLogger(AlipayPaymentService.class);

    @Override
    public PaymentForm generatePaymentData(BillingData bill)
            throws Exception {
        Assert.isNotNull(bill, "billing data cannot be null");
        logger.info("start generate alipay forward view data");
        logger.debug("[billing  : \n {} ]", bill);

        initConfig(bill);
        Long transId = transactionService
                .createTransactionFromBillingData(bill);

        PaymentForm paymentForm = new PaymentForm();
        paymentForm.setTransactionId(transId);
        paymentForm.setForwardType(FrontPageForwardType.POST);
        paymentForm.setForwardURL(PaymentConstant.GENERAL_POST_FORM_FORWARD_URL);

        String gateway = (String) bill.getConfigData().get("gateway");
        Assert.isNotNull(gateway);
        String gatewayUrl = gateway + "?_input_charset="
                + AlipayConstant.INPUT_CHARSET;
        paymentForm.setGatewayURL(gatewayUrl);

        paymentForm.setParams(this.generatePaymentForm(transId, bill));

        logger.info("finish generate payment form ");
        logger.debug("[generated payment form : \n {}]", paymentForm);

        return paymentForm;
    }


    private void initConfig(BillingData bill) {
        Assert.isNotNull(bill);
        String accountNo = bill.getAccountNo();
        Assert.isNotNull(accountNo);
        logger.debug("[accountNo = {} ]", accountNo);
        Map<String, Object> configMap = accountService.parseConfigData(accountNo);

        logger.debug("[ parsed config : ");
        for (String key : configMap.keySet())
            logger.debug(" key = {}, value = {}", key, configMap.get(key));

        bill.addConfigData(configMap);
    }

    private Map<String, String> generatePaymentForm(Long transId, BillingData bill) {
        Assert.isNotNull(transId);
        Assert.isNotNull(bill);
        Map<String, Object> config = bill.getConfigData();
        Assert.isNotNull(config);
        logger.debug("generate forward view date [bill : {}]", bill);


        Map<String, String> params = new HashMap<>();

        Integer accountType = (Integer) config.get("type");
        Assert.isNotNull(accountType);
        if (accountType.equals(PaymentType.ALIPAY_WAP_GATEWAY.getTypeId())) {
            params.put("service", AlipayConstant.SERVICE_ALIPAY_WAP_GATEWAY);
        } else {
            params.put("service", AlipayConstant.SERVICE_ALIPAY_GATEWAY);
            params.put("exter_invoke_ip", "127.0.0.1");
            params.put("anti_phishing_key", "");
        }

        params.put("seller_id", (String) config.get("partner"));
        params.put("_input_charset", AlipayConstant.INPUT_CHARSET);
        params.put("partner", (String) config.get("partner"));
        params.put("payment_type", AlipayConstant.DEFAULT_PAYMENT_TYPE.toString());

        String notificationURL = bill.getContextPath()
                .concat("/api/payment/alipay/notifications");
        params.put("notify_url", notificationURL);
        params.put(
                "return_url",
                bill.getContextPath()
                        .concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH)
                        .concat("?transId=")
                        .concat(transId.toString()));

        params.put("out_trade_no", ParamUtils.getOrderNo(
                (String) config.get("partner")));
        params.put("subject", bill.getSubject());
        params.put("body", bill.getDescription());

        Assert.isNotNull(bill.getPrice());
        Assert.isTrue(bill.getPrice().compareTo(BigDecimal.ZERO) > 0,
                "paid amount must be greater than zero");
        BigDecimal orderFee = bill.getPrice()
                .multiply(BigDecimal.valueOf(bill.getQuantity()))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        params.put("total_fee", orderFee.toString());

        params.put("show_url", (String) config.get("show_url"));
        params.put("sign_type", AlipayConstant.SIGN_TYPE);
        params.put(
                "sign",
                AlipayHelper.md5Sign(params, (String) config.get("gateway"),
                        (String) config.get("securityKey")));

        logger.trace("built parameter map : {}",
                AlipayHelper.getMapContent(params));

        return params;
    }

}
