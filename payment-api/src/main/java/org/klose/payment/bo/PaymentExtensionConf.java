package org.klose.payment.bo;

public class PaymentExtensionConf {

	private Integer accountType;

	private String prepareBillingDataBean;

	private String processPaymentCallbackBean;

	private String bizType;

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getPrepareBillingDataBean() {
		return prepareBillingDataBean;
	}

	public void setPrepareBillingDataBean(String prepareBillingDataBean) {
		this.prepareBillingDataBean = prepareBillingDataBean;
	}

	public String getProcessPaymentCallbackBean() {
		return processPaymentCallbackBean;
	}

	public void setProcessPaymentCallbackBean(String processPaymentCallbackBean) {
		this.processPaymentCallbackBean = processPaymentCallbackBean;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	@Override
	public String toString() {
		return "PaymentExtensionConf [accountType=" + accountType
				+ ", prepareBillingDataBean=" + prepareBillingDataBean
				+ ", processPaymentCallbackBean=" + processPaymentCallbackBean
				+ ", bizType=" + bizType + "]";
	}

}
