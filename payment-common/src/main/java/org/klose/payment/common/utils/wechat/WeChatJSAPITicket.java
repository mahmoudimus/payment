package org.klose.payment.common.utils.wechat;

public class WeChatJSAPITicket {
	private String ticket;
	private Long expiredTime;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Long expiredTime) {
		this.expiredTime = expiredTime;
	}

	@Override
	public String toString() {
		return "WeChatJSAPITicket [ticket=" + ticket + ", expiredTime="
				+ expiredTime + "]";
	}

	public WeChatJSAPITicket() {
		super();
	}

}
