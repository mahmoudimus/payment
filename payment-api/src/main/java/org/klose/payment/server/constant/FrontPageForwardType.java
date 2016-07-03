package org.klose.payment.server.constant;

import java.util.HashMap;

public enum FrontPageForwardType {
	GET(1), POST(2), WEIXIN_JS(3), REDIRECT(4);
	
	private int typeId;
	
	private static HashMap<Integer, FrontPageForwardType> map = new HashMap<Integer, FrontPageForwardType>();

  static {
      for (FrontPageForwardType type : FrontPageForwardType.values()) {
		  map.put(type.typeId, type);
	  }
  }

  public static FrontPageForwardType valueOf(int typeId) {
      return (FrontPageForwardType) map.get(typeId);
  }

	private FrontPageForwardType(int typeId) {
		this.typeId = typeId;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
}
