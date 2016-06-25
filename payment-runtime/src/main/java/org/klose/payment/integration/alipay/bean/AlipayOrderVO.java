package org.klose.payment.integration.alipay.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.klose.payment.integration.alipay.config.AlipayConfig;

import java.io.Serializable;
import java.math.BigDecimal;

public class AlipayOrderVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6627980670380975125L;

	@JsonProperty("orderNumber")
	private String orderNumber;

	@JsonProperty("orderSubject")
	private String orderSubject;

	@JsonProperty("bizNumber")
	private String bizNumber;

	@JsonProperty("orderBody")
	private String orderBody;

	@JsonProperty("orderFee")
	private BigDecimal orderFee;

	@JsonProperty("orderDetailURL")
	private String orderDetailURL;

	@JsonProperty("contextUrl")
	private String contextUrl;
	@JsonProperty("alipayConfig")
	private AlipayConfig config;

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderSubject() {
		return orderSubject;
	}

	public void setOrderSubject(String orderSubject) {
		this.orderSubject = orderSubject;
	}

	public String getOrderBody() {
		return orderBody;
	}

	public void setOrderBody(String orderBody) {
		this.orderBody = orderBody;
	}

	public BigDecimal getOrderFee() {
		return orderFee;
	}

	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}

	public String getOrderDetailURL() {
		return orderDetailURL;
	}

	public void setOrderDetailURL(String orderDetailURL) {
		this.orderDetailURL = orderDetailURL;
	}

	public String getBizNumber() {
		return bizNumber;
	}

	public void setBizNumber(String bizNumber) {
		this.bizNumber = bizNumber;
	}

	public String getContextUrl() {
		return contextUrl;
	}

	public void setContextUrl(String contextUrl) {
		this.contextUrl = contextUrl;
	}

	public AlipayConfig getConfig() {
		return config;
	}

	public void setConfig(AlipayConfig config) {
		this.config = config;
	}

	@Override
	public String toString() {
		return "AlipayOrderVO [orderNumber=" + orderNumber + ", orderSubject="
				+ orderSubject + ", bizNumber=" + bizNumber + ", orderBody="
				+ orderBody + ", orderFee=" + orderFee + ", orderDetailURL="
				+ orderDetailURL + ", contextUrl=" + contextUrl + ", config="
				+ config + "]";
	}

	public AlipayOrderVO() {
		super();
	}

}
