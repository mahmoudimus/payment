package org.klose.payment.constant;

import java.util.HashMap;

public enum AccountStatus {
    Ineffective(0), Effective(1), Testing(-99);

    private int statusId;

    private static HashMap<Integer, AccountStatus> map = new HashMap<>();

    static {
        for (AccountStatus status : AccountStatus.values()) {
            map.put(status.statusId, status);
        }
    }

    public static AccountStatus valueOf(int statusId) {
        return map.get(statusId);
    }


    AccountStatus(int statusId) {
        this.statusId = statusId;
    }
}
