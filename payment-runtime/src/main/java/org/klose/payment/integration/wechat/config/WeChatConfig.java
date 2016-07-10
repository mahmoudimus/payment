package org.klose.payment.integration.wechat.config;

public class WeChatConfig {
	private String appId;
	private String appSecret;
	private String mchId;
	private String securityKey;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	public WeChatConfig() {
		super();
	}

	@Override
	public String toString() {
		return "WeChatConfig [appId=" + appId + ", appSecret=" + appSecret
				+ ", mchId=" + mchId + ", securityKey=" + securityKey + "]";
	}

}
