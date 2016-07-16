package org.klose.payment.constant;

import java.util.HashMap;

public enum FrontPageForwardType {
    GET(1), POST(2), REDIRECT(3);

    private int typeId;

    private static HashMap<Integer, FrontPageForwardType> map = new HashMap<>();

    static {
        for (FrontPageForwardType type : FrontPageForwardType.values()) {
            map.put(type.typeId, type);
        }
    }

    public static FrontPageForwardType valueOf(int typeId) {
        return map.get(typeId);
    }

    FrontPageForwardType(int typeId) {
        this.typeId = typeId;
    }


}
