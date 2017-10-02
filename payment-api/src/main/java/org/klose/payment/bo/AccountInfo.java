package org.klose.payment.bo;

import org.klose.payment.constant.AccountStatus;
import org.klose.payment.constant.AccountUseType;
import org.klose.payment.constant.PaymentType;

import java.io.Serializable;

public class AccountInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2706288403362657815L;

    private String accountNo;

    private String name;

    private PaymentType type;

    private AccountUseType useType;

    private String merchantNo;

    private String merchantName;

    private AccountStatus status;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public AccountUseType getUseType() {
        return useType;
    }

    public void setUseType(AccountUseType useType) {
        this.useType = useType;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "accountNo='" + accountNo + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", useType=" + useType +
                ", merchantNo='" + merchantNo + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", status=" + status +
                '}';
    }
}
