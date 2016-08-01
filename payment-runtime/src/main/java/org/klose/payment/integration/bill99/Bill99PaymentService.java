package org.klose.payment.integration.bill99;


import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.LogUtils;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.integration.bill99.constant.Bill99Constant;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@Component("bill99PaymentService")
public class Bill99PaymentService implements PaymentService {
    @Autowired
    private TransactionDataService transactionService;
    @Autowired
    private AccountService accountService;

    private Logger logger = LoggerFactory.getLogger(Bill99PaymentService.class);


    @Override
    public PaymentForm generatePaymentData(BillingData bill) {
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
        paymentForm.setGatewayURL(gateway);

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

        logger.debug("[ parsed config : {}", LogUtils.getMapContent(configMap));
        bill.addConfigData(configMap);
    }

    private Map<String, String> generatePaymentForm(Long transId, BillingData bill) {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("inputCharset", Bill99Constant.INPUT_CHARSET);

        params.put("version", Bill99Constant.VERSION);
        params.put("language", Bill99Constant.LANGUAGE);
        params.put("signType", Bill99Constant.SIGN_TYPE);

        // 服务器接收分账结果的后台地址
        String hostEndpoint = bill.getContextPath();
        Assert.isNotNull(hostEndpoint);
        //final String PATH_NOTIFY_API = "/api/99bill/notifications";
        //params.put("bgUrl", hostEndpoint.concat(PATH_NOTIFY_API));
        //for test
        params.put("bgUrl", "http://requestb.in/y20pycy2");
        // 服务器回调前台地址
        String pageUrl = hostEndpoint.concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH).concat("?transId=")
                .concat(transId.toString());
        //params.put("pageUrl", pageUrl);
        params.put("merchantAcctId", (String) bill.getConfigData().get("merchantAcctId"));

        params.put("orderId", bill.getBizNo()); // quotation number
        BigDecimal amount = bill.getPrice().multiply(BigDecimal.valueOf(100));
        params.put("orderAmount", String.valueOf(amount.intValue()));
        params.put("orderTime", DateUtil.format(new Date(),
                DateUtil.DATE_TIME_FORMAT_COMPACT_S));

        params.put("productName", bill.getSubject());
        params.put("productNum", String.valueOf(bill.getQuantity()));
        params.put("productDesc", bill.getDescription());
        params.put("ext1", (String) bill.getExtData().get("channelCode"));
        params.put("ext2", (String) bill.getExtData().get("productId"));
        params.put("payType", Bill99Constant.PAY_TYPE);

        String signature = Bill99Helper.sign(params, (String)bill.getConfigData().get("privateKeyPath"),
                (String)bill.getConfigData().get("privateKeyPassword"));
        Assert.isNotNull(signature);
        params.put("signMsg", signature);
        return params;
    }


}
