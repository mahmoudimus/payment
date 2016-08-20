package org.klose.payment.po;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "T_PAYMENT_TRANSACTION")
public class TransactionPO implements Serializable {

	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	@Column
	private String accountNo;

	@Column(name = "orderNo")
	@Index(name = "orderNo")
	private String transactionNo;

	@Column
	private String bizNo;

	@Column
	private String subject;

	@Column
	private BigDecimal amount;

	@Column
	private String prePayId;

	@Column
	private String payId;

	@Column
	private String callBackAgent;

	@Column
	private Integer status;

	@Column(nullable = false)
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String messageLog;

	@Column
	private Date creation_time;

	@Column
	private Date completion_time;

	@Column
	private String currency;

	@Column
	private String notificationMsg;
	
	@Column
	private String returnURL;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getBizNo() {
		return bizNo;
	}

	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPrePayId() {
		return prePayId;
	}

	public void setPrePayId(String prePayId) {
		this.prePayId = prePayId;
	}

	public String getPayId() {
		return payId;
	}

	public void setPayId(String payId) {
		this.payId = payId;
	}

	public String getCallBackAgent() {
		return callBackAgent;
	}

	public void setCallBackAgent(String callBackAgent) {
		this.callBackAgent = callBackAgent;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessageLog() {
		return messageLog;
	}

	public void setMessageLog(String messageLog) {
		this.messageLog = messageLog;
	}

	public Date getCreation_time() {
		return creation_time;
	}

	public void setCreation_time(Date creation_time) {
		this.creation_time = creation_time;
	}

	public Date getCompletion_time() {
		return completion_time;
	}

	public void setCompletion_time(Date completion_time) {
		this.completion_time = completion_time;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getNotificationMsg() {
		return notificationMsg;
	}

	public void setNotificationMsg(String notificationMsg) {
		this.notificationMsg = notificationMsg;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionPO other = (TransactionPO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
