package org.klose.payment.integration.wechat;


import org.apache.commons.lang3.CharEncoding;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.util.MD5Util;
import org.klose.payment.util.ParamUtils;
import org.klose.payment.util.ValidateCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component(value = "WeixinJSApiPaymentService")
public class WeixinJSApiPaymentService implements PaymentService {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountDao accountDao;

    @Autowired
    TransactionDataService transactionService;

    private final static Logger logger = LoggerFactory.getLogger(WeixinJSApiPaymentService.class);

    @Override
    public PaymentForm generatePaymentData(BillingData bill) {
        Assert.isNotNull(bill);
        logger.info("start generate wechat payment form");
        logger.debug("[billing : \n {}]", bill);
        initConfig(bill);

        PaymentForm viewData = new PaymentForm();
        viewData.setForwardType(FrontPageForwardType.POST);
        viewData.setForwardURL(WeChatConstant.WEIXIN_JSAPI_FORWARD_URL);
        viewData.setParams(this.generatePaymentForm(bill));
        Long transactionId = transactionService.createTransactionFromBillingData(bill);
        viewData.setTransactionId(transactionId);
        viewData.setReturnURL(bill.getContextPath().concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH).
                concat("?transId=").concat(transactionId.toString()));

        return viewData;
    }

    @SuppressWarnings("unchecked")
    private WechatPrepayData generatePrepay(BillingData bill) {
        Map<String, Object> accountConfig = bill.getConfigData();
        Assert.isNotNull(accountConfig);
        Map<String, Object> extData = bill.getExtData();
        Assert.isNotNull(extData);

        WechatPrepayData prepayData = new WechatPrepayData();
        String mchId = (String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_MCH_ID);
        Assert.isNotNull(mchId);
        prepayData.setOrderNo(ParamUtils.getOrderNo(mchId));
        bill.setOrderNo(prepayData.getOrderNo());
        prepayData.setProductName(bill.getSubject());
        prepayData.setTotal_fee(bill.getPrice().multiply(
                new BigDecimal(bill.getQuantity())));
        prepayData.setTradeType(WeChatConstant.WEIXIN_TRADETYPE_JSAPI);
        prepayData.setProductId((String) (extData
                .get(WeChatConstant.KEY_WEIXIN_PRODUCT_ID)));

        prepayData.setAppId((String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_APP_ID));
        prepayData.setMchId(mchId);
        prepayData.setMchSecret((String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_SECURITY));
        prepayData.setOpenId((String) (extData
                .get(WeChatConstant.KEY_WEIXIN_OPENID)));
        return prepayData;
    }

    private WechatPrepayResponseDto prepay(BillingData bill) {
        Assert.isNotNull(bill);
        WechatPrepayData prepay = generatePrepay(bill);
        logger.debug("prepay : \n {} ", prepay);
        WechatPrepayResponseDto prepayResponse;
        try {
            prepayResponse = WechatPrepayService.getPrepay(
                    prepay, bill.getContextPath());
        } catch (Exception e) {
            logger.error("exception occurs by wechat prepay", e);
            throw new RuntimeException(e);
        }
        Assert.isNotNull(prepayResponse);
        logger.debug("prepay response : \n {}", prepayResponse);
        return prepayResponse;
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

    private Map<String, String> generatePaymentForm(BillingData bill) {
        Assert.isNotNull(bill);
        logger.debug("[billing data : \n {} ]", bill);
        WechatPrepayResponseDto prepayResponse = prepay(bill);
        Assert.isNotNull(prepayResponse);

        String nonceStr = ValidateCode.randomCode(10);
        String timeStamp = String.valueOf(new Date().getTime() / 1000);

        //@TODO extract sign logic
        Map<String, String> parmameters = new TreeMap<>();
        parmameters.put("appId", prepayResponse.getAppId());
        parmameters.put("timeStamp", timeStamp);
        parmameters.put("nonceStr", nonceStr);
        parmameters.put("package", "prepay_id=" + prepayResponse.getPrepay_id());
        parmameters.put("signType", "MD5");

        String param_str = ParamUtils.buildParams(parmameters) + "&key=" + prepayResponse.getSecurityKey();
        String paySign = MD5Util.MD5Encode(param_str, CharEncoding.UTF_8).toUpperCase();
        parmameters.put("paySign", paySign);
        return parmameters;
    }
}
