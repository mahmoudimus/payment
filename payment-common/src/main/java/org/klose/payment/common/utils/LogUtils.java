package org.klose.payment.common.utils;

import java.util.Map;

public class LogUtils {
    @SuppressWarnings("rawtypes")
    public static String getMapContent(Map map) {
        StringBuilder builder = new StringBuilder();
        for (Object key : map.keySet())
            builder.append("key = ").append(key).append(" value = ").append(map.get(key)).append("\n");

        return builder.toString();
    }
}
