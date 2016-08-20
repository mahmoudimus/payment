package org.klose.payment.po;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "T_PAYMENT_ACCOUNT")
public class AccountPO implements Serializable {

	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	@Column
	@Index(name = "accountNo")
	private String accountNo;
	
	@Column
	private String name;
	
	@Column
	private Integer type;
	
	@Column
	private Integer useType;
	
	@Column
	private String gatewayURL;

	@Column(nullable = false)
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private String configData;

	@Column
	private String merchantNo;
	
	@Column
	private String merchantName;
	
	@Column
	private String processBean;
	
	@Column(name = "status")
	private Integer statusId;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUseType() {
		return useType;
	}

	public void setUseType(Integer useType) {
		this.useType = useType;
	}

	public String getGatewayURL() {
		return gatewayURL;
	}

	public void setGatewayURL(String gatewayURL) {
		this.gatewayURL = gatewayURL;
	}

	public String getConfigData() {
		return configData;
	}

	public void setConfigData(String configData) {
		this.configData = configData;
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

	public String getProcessBean() {
		return processBean;
	}

	public void setProcessBean(String processBean) {
		this.processBean = processBean;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
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
		AccountPO other = (AccountPO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
