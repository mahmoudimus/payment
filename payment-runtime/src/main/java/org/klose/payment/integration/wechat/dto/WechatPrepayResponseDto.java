package org.klose.payment.integration.wechat.dto;

import java.io.Serializable;

/**
 * 获取预订单返回
 *
 * @author Administrator
 */

public class WechatPrepayResponseDto implements Serializable{

    private String return_code;

    private String prepay_id;

    private String sign;

    private String appId;

    private String securityKey;

    private Integer amount;

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "WechatPrepayResponseDto{" +
                "return_code='" + return_code + '\'' +
                ", prepay_id='" + prepay_id + '\'' +
                ", sign='" + sign + '\'' +
                ", appId='" + appId + '\'' +
                ", securityKey='" + securityKey + '\'' +
                ", amount=" + amount +
                '}';
    }
}
