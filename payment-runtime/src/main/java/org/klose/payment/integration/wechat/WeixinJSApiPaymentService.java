package org.klose.payment.integration.wechat;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.common.utils.JSONHelper;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.integration.wechat.dto.WechatPayDto;
import org.klose.payment.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.po.AccountPO;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.util.ParamUtils;
import org.klose.payment.util.ValidateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(value = "WeixinJSApiPaymentService")
public class WeixinJSApiPaymentService implements PaymentService {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountDao accountDao;

    @Autowired
    TransactionDataService transactionService;

    @Override
    public PaymentForm generatePaymentData(BillingData bill)
            throws Exception {
        WechatPrepayData prepayData = convertBillingData(bill);

        WechatPrepayResponseDto prepayResponse = WechatPrepayService.getPrepay(
                prepayData, bill.getContextPath());

        Map<String, Object> extData = bill.getExtData() != null ? bill
                .getExtData() : new HashMap<String, Object>(1);

        extData.put(WeChatConstant.KEY_WEIXIN_PREPAY_ID,
                prepayResponse.getPrepay_id());

        PaymentForm result = generateForwardViewData(prepayResponse);

        result.setTransactionId(transactionService
                .createTransactionFromBillingData(bill));

        return result;
    }

    @SuppressWarnings("unchecked")
    private WechatPrepayData convertBillingData(BillingData bill) {
        AccountPO account = accountDao.findByAccountNo(bill.getAccountNo());
        Map<String, Object> accountConfig = (Map<String, Object>) JSONHelper
                .parse(account.getConfigData());

        Map<String, Object> extData = bill.getExtData();

        WechatPrepayData result = new WechatPrepayData();
        String mchId = (String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_MCH_ID);
        Assert.isNotNull(mchId);
        // result.setOrderNo(bill.getBizNo());
        result.setOrderNo(ParamUtils.getOrderNo(mchId));
        bill.setOrderNo(result.getOrderNo());
        result.setProductName(bill.getSubject());
        result.setTotal_fee(bill.getPrice().multiply(
                new BigDecimal(bill.getQuantity())));
        result.setTradeType(WeChatConstant.WEIXIN_TRADETYPE_JSAPI);
        result.setProductId((String) (extData
                .get(WeChatConstant.KEY_WEIXIN_PRODUCT_ID)));

        result.setAppId((String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_APP_ID));
        result.setMchId(mchId);
        result.setMchSecret((String) accountConfig
                .get(WeChatConstant.KEY_WEIXIN_SECURITY));
        result.setOpenId((String) (extData
                .get(WeChatConstant.KEY_WEIXIN_OPENID)));

        return result;
    }

    private PaymentForm generateForwardViewData(
            WechatPrepayResponseDto prepayResponse) {

        String nonceStr = ValidateCode.randomCode(10);
        String timeStamp = String.valueOf(new Date().getTime() / 1000);

        WechatPayDto payDto = new WechatPayDto(prepayResponse.getAppId(),
                prepayResponse.getSecurityKey());
        payDto.setNonceStr(nonceStr);
        payDto.setTimeStamp(timeStamp);
        payDto.setPackage_str("prepay_id=" + prepayResponse.getPrepay_id());
        payDto.setSignType("MD5");
        payDto.setPaySign(payDto.createSign());

        Map<String, String> PayDtoResult = new HashMap<String, String>();
        PayDtoResult.put("appId", payDto.getAppId());
        PayDtoResult.put("timeStamp", timeStamp);
        PayDtoResult.put("nonceStr", nonceStr);
        PayDtoResult.put("package", payDto.getPackage_str());
        PayDtoResult.put("signType", payDto.getSignType());
        PayDtoResult.put("paySign", payDto.getPaySign());

        PaymentForm viewData = new PaymentForm();

        viewData.setForwardType(FrontPageForwardType.WEIXIN_JS);
        viewData.setForwardURL(WeChatConstant.WEIXIN_JSAPI_FORWARD_URL);

        viewData.setParams(PayDtoResult);

        return viewData;
    }
}
