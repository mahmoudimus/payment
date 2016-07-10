package org.klose.payment.constant;

import java.util.HashMap;

public enum AccountUseType {
	Collection(1), Payment(2);
	
	private int typeId;
	
	private static HashMap<Integer, AccountUseType> map = new HashMap<Integer, AccountUseType>();

  static {
      for (AccountUseType type : AccountUseType.values()) {
          map.put(type.typeId, type);
      }
  }

  public static AccountUseType valueOf(int typeId) {
      return (AccountUseType) map.get(typeId);
  }

	public int getUseTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	private AccountUseType(int typeId) {
		this.typeId = typeId;
	}
}
