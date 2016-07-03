package org.klose.payment.server.constant;

import java.util.HashMap;

public enum PaymentStatus {
	Proceed(0), Success(1), Failed(2), Completed(-1);
	
	private int statusId;
	
	private static HashMap<Integer, PaymentStatus> map = new HashMap<Integer, PaymentStatus>();

  static {
      for (PaymentStatus status : PaymentStatus.values()) {
          map.put(status.statusId, status);
      }
  }

  public static PaymentStatus valueOf(int statusId) {
      return (PaymentStatus) map.get(statusId);
  }

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	private PaymentStatus(int statusId) {
		this.statusId = statusId;
	}
}
