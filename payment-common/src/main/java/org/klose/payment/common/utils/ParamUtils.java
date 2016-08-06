package org.klose.payment.common.utils;


import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class ParamUtils {

	@SuppressWarnings("rawtypes")
	public static String buildParams(Map<String, String> params) {

		StringBuilder sb = new StringBuilder();
		for (Object o : params.entrySet()) {
			Map.Entry entry = (Map.Entry) o;
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();

			if (sb.length() == 0 && !StringUtils.isEmpty(v)) {
				sb.append(k).append("=").append(v);
			} else if (sb.length() > 0 && !StringUtils.isEmpty(v)) {
				sb.append("&").append(k).append("=").append(v);
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String buildParams(Map<String, String> params, String[] keys) {
		Assert.isNotNull(params);
		Assert.isNotNull(keys);

		StringBuilder builder = new StringBuilder();
		for (String paramName : keys) {
			String paramValue = params.get(paramName);
			if (!StringUtils.isEmpty(paramValue)) {
				if (builder.length() > 0)
					builder.append("&");

				builder.append(paramName).append("=").append(paramValue);
			}
		}
		return builder.toString();
	}

	/**
	 * 随机16为数值
	 * 
	 * @return
	 */
	public static String buildRandom() {
		String currTime = DateUtil.getCurrTime();
		String strTime = currTime.substring(8, currTime.length());
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < 4; i++) {
			num = num * 10;
		}
		return (int) ((random * num)) + strTime;
	}

	public static String getOrderNo(String mchId) {
		String order = mchId
				+ new SimpleDateFormat("yyyyMMdd").format(new Date());
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			order += r.nextInt(9);
		}
		return order;
	}

}
