package org.klose.payment.constant;

import java.util.HashMap;

public enum AccountStatus {
    Ineffective(0), Effective(1), Testing(-99);

    private int statusId;

    private static HashMap<Integer, AccountStatus> map = new HashMap<Integer, AccountStatus>();

    static {
        for (AccountStatus status : AccountStatus.values()) {
            map.put(status.statusId, status);
        }
    }

    public static AccountStatus valueOf(int statusId) {
        return (AccountStatus) map.get(statusId);
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    AccountStatus(int statusId) {
        this.statusId = statusId;
    }
}
