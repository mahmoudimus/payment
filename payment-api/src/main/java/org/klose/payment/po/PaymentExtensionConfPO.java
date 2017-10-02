package org.klose.payment.po;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "T_PAYMENT_EXT_CONF")
public class PaymentExtensionConfPO implements Serializable {


	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;

	@Column(name="accountType")
	private Integer accountType;

	@Column(name="prepareBillingDataBean")
	private String prepareBillingDataBean;

	@Column(name="processPaymentCallbackBean")
	private String processPaymentCallbackBean;

	@Column(name="bizType")
	private String bizType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return true;
	}

}
