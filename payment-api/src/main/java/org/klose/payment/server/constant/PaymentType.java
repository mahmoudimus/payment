package org.klose.payment.server.constant;

import java.util.HashMap;

public enum PaymentType {
	WX_JSAPI(1),
	WX_NATIVE(2),
	ALIPAY_GATEWAY(3),
	KUAIQIAN_GATEWAY(4),
	/** 安盛天平微信支付 */
	AXATP_WECHAT(5),
	/** 安盛天平快钱支付(借记卡) */
	AXATP_99BILL_EB(6),
	/** 安盛天平快钱支付(信用卡) */
	AXATP_99BILL_CC(7),
	ZHONGAN_GATEWAY(8),
	CCIC_GATEWAY(9),
	ALIPAY_WAP_GATEWAY(10),
	CHNLPPAY_WAP_GATEWAY(11),
	CHNLPPAY_WEB_GATEWAY(12),
	BANKCOMM_GATEWAY(13);

	private int typeId;

	private static HashMap<Integer, PaymentType> map = new HashMap<Integer, PaymentType>();

	static {
		for (PaymentType type : PaymentType.values()) {
			map.put(type.typeId, type);
		}
	}

	public static PaymentType valueOf(int typeId) {
		return (PaymentType) map.get(typeId);
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	private PaymentType(int typeId) {
		this.typeId = typeId;
	}
}
