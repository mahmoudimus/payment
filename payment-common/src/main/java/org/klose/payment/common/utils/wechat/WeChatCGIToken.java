package org.klose.payment.common.utils.wechat;

public class WeChatCGIToken {
	private String token;
	private Long expiredTime;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}

	@Override
	public String toString() {
		return "WeChatCGIToken [token=" + token + ", expiredTime="
				+ expiredTime + "]";
	}

	public WeChatCGIToken() {
		super();
	}

}
