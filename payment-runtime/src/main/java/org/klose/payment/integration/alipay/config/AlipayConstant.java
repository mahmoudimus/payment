package org.klose.payment.integration.alipay.config;

public interface AlipayConstant {
    // 字符编码格式 目前支持 gbk 或 utf-8
    String INPUT_CHARSET = "utf-8";
    // 签名方式
    String SIGN_TYPE = "MD5";
    //PC页面支付集成
    String SERVICE_ALIPAY_GATEWAY = "create_direct_pay_by_user";
    //手机页面支付集成
    String SERVICE_ALIPAY_WAP_GATEWAY = "alipay.wap.create.direct.pay.by.user";

    Integer DEFAULT_PAYMENT_TYPE = 1;
}
