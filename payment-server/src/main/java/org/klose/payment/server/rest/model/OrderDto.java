package org.klose.payment.server.rest.model;

public class OrderDto {
	private String accountNo;
	private String bizNo;
	private String bizType;
	private String returnURL;

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBizNo() {
		return bizNo;
	}

	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}

	public String getBizType() {
		return bizType;
	}

	public void setBizType(String bizType) {
		this.bizType = bizType;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	@Override
	public String toString() {
		return "OrderDto [accountNo=" + accountNo + ", bizNo=" + bizNo
				+ ", bizType=" + bizType + ", returnURL=" + returnURL + "]";
	}

}
