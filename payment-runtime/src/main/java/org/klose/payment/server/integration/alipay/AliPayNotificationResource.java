package org.klose.payment.server.integration.alipay;


import org.klose.payment.server.common.utils.Assert;
import org.klose.payment.server.dao.TransactionDao;
import org.klose.payment.server.integration.alipay.config.AlipayConfig;
import org.klose.payment.server.integration.alipay.util.AlipayHelper;
import org.klose.payment.server.po.TransactionPO;
import org.klose.payment.server.service.callback.ProcessNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Path("payment/alipay")
@Component
public class AliPayNotificationResource {

	@Autowired
	private ProcessNotificationService notificationService;
	@Autowired
	private TransactionDao dataDao;
	@Autowired
	private AlipayConfigService configService;

	private final static Logger logger = LoggerFactory
			.getLogger(AliPayNotificationResource.class);

	@POST
	@Path("/notifications")
	@Consumes("application/x-www-form-urlencoded")
	public Response createNotification(@Context HttpServletRequest request)
			throws UnsupportedEncodingException {
		Assert.isNotNull(request);
		Map<String, String> params = this.parseRequest(request);
		Assert.isNotNull(params, "params parsed by request is null");
		logger.debug("receive notification + [params: {} ]",
				AlipayHelper.getMapContent(params));

		// 核心产生的交易号
		String orderNo = params.get("out_trade_no");
		Assert.isNotNull(orderNo);
		logger.trace("parsed orderNo = {}", orderNo);
		AlipayConfig config = this.getConfig(orderNo);
		Assert.isNotNull(config, " account config is null");
		logger.trace("alipay Config : {}", config);

		String tradeStatus = new String(params.get("trade_status").getBytes(
				"ISO-8859-1"), "UTF-8");
		Assert.isNotNull(tradeStatus);
		Map<String, String> notifyMap = this.buildNotifyMap(params);
		String notifyData = this.transMapToString(notifyMap);

		logger.trace("tradeStatus = {}, notfiyMap : {}, notifyData = {}",
				tradeStatus, AlipayHelper.getMapContent(notifyMap), notifyData);

		String payId = null;

		logger.trace(
				"payId = {}, orderNo = {}, notifyMap: {}, notifyDate = {}",
				payId, orderNo, AlipayHelper.getMapContent(notifyMap),
				notifyData);

		if (AlipayHelper.verifyNotification(params, config.getGateway(),
				config.getPartner(), config.getKey())) {
			// 交易状态 TRADE_FINISHED 的通知触发条件是商户签约的产品不支持退款
			// 功能的前提下,买家付款成功;或者,商户签约的产品支持退款功能的前提下,
			// 交易已经成功并且已经超过可退款期限;
			// 交易状态 TRADE_SUCCESS 的通知触发条件是商户签约的产品支持退款功能的前提下,买家付款成功;
			if (tradeStatus.equals("TRADE_FINISHED")
					|| tradeStatus.equals("TRADE_SUCCESS"))
				payId = params.get("trade_no"); // 阿里产生的交易号

			notificationService.handlePaymentCallback(orderNo, notifyData,
					payId, tradeStatus, notifyMap);
			return Response.ok("success", MediaType.TEXT_HTML).build();
		} else {
			notificationService.handlePaymentCallback(orderNo, notifyData,
					payId, "verification failed", notifyMap);
			throw new RuntimeException(
					"alipay notfiy resposne verfication failed");
		}

	}

	@SuppressWarnings("rawtypes")
	private Map<String, String> parseRequest(HttpServletRequest request) {
		Assert.isNotNull("request", "request from alipay can not be null");
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, valueStr);
		}
		logger.trace("parsed request params : {}",
				AlipayHelper.getMapContent(params));
		return params;
	}

	private String transMapToString(Map<String, String> map) {
		Assert.isNotNull(map, "map can not be null");
		logger.trace("map : {}", AlipayHelper.getMapContent(map));

		StringBuilder sb = new StringBuilder();
		for (String key : map.keySet()) {
			sb.append(key).append("=")
					.append(null == map.get(key) ? "" : map.get(key))
					.append("^");
		}

		logger.trace("built string = {}", sb.toString());

		return sb.toString();
	}

	private AlipayConfig getConfig(String orderNo) {
		Assert.isNotNull(orderNo, "order no is null");
		logger.trace("orderNo = {}", orderNo);
		TransactionPO po = dataDao.findByTransactionNo(orderNo);
		Assert.isNotNull(po, "payment transaction is null");
		Assert.isNotNull(po.getAccountNo(),
				"accountNo in transactionPO is null");

		logger.trace("account PO : {}", po);
		AlipayConfig config = configService.initializeConfig(po.getAccountNo());

		logger.trace("quried alipay config : {}", config);
		return config;
	}

	private Map<String, String> buildNotifyMap(Map<String, String> params) {
		Assert.isNotNull(params);
		logger.trace("params : {}", AlipayHelper.getMapContent(params));
		Map<String, String> map = new HashMap<String, String>();
		String[] keys = new String[] { "seller_email", "subject", "body",
				"buyer_id", "quantity", "discount", "total_fee",
				"payment_type", "trade_no", "price", "seller_id",
				"trade_status", "notify_id", "out_trade_no", "buyer_email" };
		for (String key : keys)
			map.put(key, params.get(key));

		logger.trace("built notify map : {}",
				AlipayHelper.getMapContent(params));
		return map;
	}
}
