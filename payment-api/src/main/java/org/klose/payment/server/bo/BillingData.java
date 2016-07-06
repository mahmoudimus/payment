package org.klose.payment.server.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BillingData implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7114949567821930581L;

    private String accountNo;

    private Integer accountType;

    // pass by business module
    private String bizNo;
    private String bizType;

    // created by payment module
    private String orderNo;

    private String subject;

    private String description;

    private String currency;

    private BigDecimal price;

    private int quantity = 1;

    private String hostEndpoint;

    private String returnURL;

    private String prepareService;
    private String callBackAgent;

    private Date businessEffectiveDate;

    private Map<String, Object> extData;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getHostEndpoint() {
        return hostEndpoint;
    }

    public void setHostEndpoint(String hostEndpoint) {
        this.hostEndpoint = hostEndpoint;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public String getCallBackAgent() {
        return callBackAgent;
    }

    public void setCallBackAgent(String callBackAgent) {
        this.callBackAgent = callBackAgent;
    }

    public Date getBusinessEffectiveDate() {
        return businessEffectiveDate;
    }

    public void setBusinessEffectiveDate(Date businessEffectiveDate) {
        this.businessEffectiveDate = businessEffectiveDate;
    }

    public Map<String, Object> getExtData() {
        return extData;
    }

    public String getPrepareService() {
        return prepareService;
    }

    public void setPrepareService(String prepareService) {
        this.prepareService = prepareService;
    }

    public void setExtData(Map<String, Object> extData) {
        this.extData = extData;
    }

    public void addExtData(String key, Object value) {
        if (this.extData == null) {
            this.extData = new HashMap<String, Object>();
        }

        this.extData.put(key, value);
    }

    @Override
    public String toString() {
        return "BillingData{" +
                "accountNo='" + accountNo + '\'' +
                ", accountType=" + accountType +
                ", bizNo='" + bizNo + '\'' +
                ", bizType='" + bizType + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", currency='" + currency + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", hostEndpoint='" + hostEndpoint + '\'' +
                ", returnURL='" + returnURL + '\'' +
                ", prepareService='" + prepareService + '\'' +
                ", callBackAgent='" + callBackAgent + '\'' +
                ", businessEffectiveDate=" + businessEffectiveDate +
                ", extData=" + extData +
                '}';
    }
}
