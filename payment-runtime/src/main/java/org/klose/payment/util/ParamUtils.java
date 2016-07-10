package org.klose.payment.util;


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParamUtils {

	@SuppressWarnings("rawtypes")
	public static String buildParams(Map<String, String> params) {

		StringBuffer sb = new StringBuffer();
		Iterator it = params.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();

			if (sb.length() == 0 && !StringUtils.isEmpty(v)) {
				sb.append(k + "=" + v);
			} else if (sb.length() > 0 && !StringUtils.isEmpty(v)) {
				sb.append("&" + k + "=" + v);
			}
		}
		return sb.toString();
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

	public static String createXML(Map<String, Object> map) {
		String xml = "<xml>";
		Set<String> set = map.keySet();
		Iterator<String> i = set.iterator();
		while (i.hasNext()) {
			String str = i.next();
			xml += "<" + str + ">" + "<![CDATA[" + map.get(str) + "]]>" + "</"
					+ str + ">";
		}
		xml += "</xml>";
		return xml;
	}

	/**
	 * 获取ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("PRoxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (null == ip) {
			ip = "";
		}
		if (StringUtils.isNotEmpty(ip)) {
			String[] ipArr = ip.split(",");
			if (ipArr.length > 1) {
				ip = ipArr[0];
			}
		}
		return ip;
	}

	public static String createSign(Map<String, Object> map, String key) {
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		for (Map.Entry<String, Object> m : map.entrySet()) {
			packageParams.put(m.getKey(), m.getValue().toString());
		}

		StringBuffer sb = new StringBuffer();
		Set<?> es = packageParams.entrySet();
		Iterator<?> it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!StringUtils.isEmpty(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + key);

		String sign = MD5Util.MD5Encode(sb.toString(),
				StandardCharsets.UTF_8.name()).toUpperCase();
		return sign;
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
