package org.klose.payment.constant;

import java.util.HashMap;

public enum PaymentType {
	WX_JSAPI(1),
	WX_NATIVE(2),
	ALIPAY_GATEWAY(3),
	ALIPAY_WAP_GATEWAY(4),
	BILL99_GATEWAY(5);

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
