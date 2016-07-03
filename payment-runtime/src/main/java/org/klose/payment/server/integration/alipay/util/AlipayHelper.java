package org.klose.payment.server.integration.alipay.util;

import org.klose.payment.server.common.utils.Assert;


import org.klose.payment.server.integration.alipay.config.AlipayConstant;
import org.klose.payment.server.integration.alipay.sign.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class AlipayHelper {
	private final static Logger logger = LoggerFactory
			.getLogger(AlipayHelper.class);

	private static boolean isNotifyIdValid(String notifyId, String gateway,
			String partner) {
		Assert.isNotNull(notifyId);
		Assert.isNotNull(gateway);
		Assert.isNotNull(partner);
		if (logger.isTraceEnabled())
			logger.trace("starting verfiy notifyId. [notifyId = " + notifyId
					+ " gateway = " + gateway + " partner = " + partner + " ]");

		String verifyURL = String.format(
				"%s?service=notify_verify&partner=%s&notify_id=%s", gateway,
				partner, notifyId);

		String inputLine = "";

		try {
			URL url = new URL(verifyURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			inputLine = "";
		}

		if (logger.isTraceEnabled())
			logger.trace("[inputLine = " + inputLine + "]");

		return inputLine.equals("true");
	}

	private static boolean isNotifySignatureValid(Map<String, String> params,
			String signature, String secretKey) {
		Assert.isNotNull(params);
		Assert.isNotNull(signature);
		Assert.isNotNull(secretKey);

		if (logger.isTraceEnabled())
			logger.trace("start verify signature... [params: "
					+ getMapContent(params) + " signature = " + signature
					+ " secretKey = " + secretKey);

		Map<String, String> filteredParams = AlipayCore.paraFilter(params);
		String paramsStr = AlipayCore.createLinkString(filteredParams);

		if (logger.isTraceEnabled())
			logger.trace("[filteredParams: " + getMapContent(filteredParams)
					+ " paramStr = " + paramsStr);

		if (AlipayConstant.SIGN_TYPE.equals("MD5")) {
			return MD5.verify(paramsStr, signature, secretKey,
					AlipayConstant.INPUT_CHARSET);
		}

		return false;
	}

	public static String md5Sign(Map<String, String> params, String gateway,
			String secretKey) {
		Assert.isNotNull(params);
		Assert.isNotNull(gateway);
		Assert.isNotNull(secretKey);

		if (logger.isDebugEnabled())
			logger.debug("build request url... [params: "
					+ getMapContent(params) + " gateway = " + gateway
					+ " secretKey = " + secretKey + "]");

		Map<String, String> filteredParams = AlipayCore.paraFilter(params);
		if (logger.isTraceEnabled())
			logger.trace("intialized filterParams :"
					+ getMapContent(filteredParams));

		String paramsStr = AlipayCore.createLinkString(filteredParams);

		Assert.isNotNull(paramsStr);
		String signatureOfParams = MD5.sign(paramsStr, secretKey,
				AlipayConstant.INPUT_CHARSET);

		if (logger.isDebugEnabled())
			logger.debug("paramsStr = " + paramsStr + " signatureOfParams = "
					+ signatureOfParams);

		return signatureOfParams;
	}

	public static boolean verifyNotification(Map<String, String> params,
			String gateway, String partner, String secretKey) {
		Assert.isNotNull(params);
		Assert.isNotNull(gateway);
		Assert.isNotNull(partner);
		Assert.isNotNull(secretKey);

		if (logger.isDebugEnabled())
			logger.debug("verify notification... [params: "
					+ getMapContent(params) + " gateway = " + gateway
					+ " partner = " + partner + " secretKey = " + secretKey
					+ "]");
		if (params.get("notify_id") != null) {
			String notifyId = params.get("notify_id");
			if (isNotifyIdValid(notifyId, gateway, partner)) {
				if (params.get("sign") != null) {
					String signature = params.get("sign");
					if (isNotifySignatureValid(params, signature, secretKey)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static String getMapContent(Map map) {
		StringBuilder builder = new StringBuilder();
		for (Object key : map.keySet())
			builder.append("key = " + key + " value = " + map.get(key) + "\n");

		return builder.toString();
	}
}
