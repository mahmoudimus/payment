package org.klose.payment.server.bo;


import org.klose.payment.server.constant.PaymentStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7663772067848816450L;

	private Long transactionId;

	private String orderNo;

	private String bizNo;

	private PaymentStatus status;

	private String payId;

	private BigDecimal amount;

	private Date completionTime;

	private String errorMsg;

	private Map<String, String> extData;

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Map<String, String> getExtData() {
		return extData;
	}

	public String getBizNo() {
		return bizNo;
	}

	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}

	public void setExtData(Map<String, String> extData) {
		this.extData = extData;
	}

	public void addExtData(String key, String value) {
		if (this.extData == null) {
			this.extData = new HashMap<String, String>();
		}

		this.extData.put(key, value);
	}

	@Override
	public String toString() {
		return "PaymentResult [transactionId=" + transactionId + ", orderNo="
				+ orderNo + ", bizNo=" + bizNo + ", status=" + status
				+ ", payId=" + payId + ", amount=" + amount
				+ ", completionTime=" + completionTime + ", errorMsg="
				+ errorMsg + ", extData=" + extData + "]";
	}

}
