package org.klose.payment.constant;

import java.util.HashMap;

public enum PaymentStatus {
    Proceed(0), Success(1), Failed(2), Completed(-1);

    private int statusId;

    private static HashMap<Integer, PaymentStatus> map = new HashMap<>();

    static {
        for (PaymentStatus status : PaymentStatus.values()) {
            map.put(status.statusId, status);
        }
    }

    public static PaymentStatus valueOf(int statusId) {
        return map.get(statusId);
    }

    public int getStatusId() {
        return statusId;
    }

    PaymentStatus(int statusId) {
        this.statusId = statusId;
    }
}
