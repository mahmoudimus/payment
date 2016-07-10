package org.klose.payment.integration.alipay;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.integration.alipay.config.AlipayConstant;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.integration.alipay.bean.AlipayOrderVO;
import org.klose.payment.integration.alipay.config.AlipayConfig;
import org.klose.payment.integration.alipay.util.AlipayHelper;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.util.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component(value = "aliGateWayPaymentService")
public class AlipayPaymentService implements PaymentService {
	@Autowired
	private AlipayConfigService configService;
	@Autowired
	private TransactionDataService transactionService;

	private final static Logger logger = LoggerFactory
			.getLogger(AlipayPaymentService.class);

	final static String SERVICE_ALIPAY_GATEWAY = "create_direct_pay_by_user";

	final static String SERVICE_ALIPAY_WAP_GATEWAY = "alipay.wap.create.direct.pay.by.user";

	@Override
	public PaymentForm generatePaymentData(BillingData bill)
			throws Exception {
		Assert.isNotNull(bill, "billing data cannot be null");
		logger.info("start generate alipay forward view data");
		logger.debug("[billingData : {} ]", bill);
		AlipayConfig config = configService.initializeConfig(bill
				.getAccountNo());
		Assert.isNotNull(config);
		logger.trace("alipay config : {}", config);

		AlipayOrderVO vo = this.convert(bill, config.getPartner());
		Assert.isNotNull(vo);
		vo.setConfig(config);
		logger.trace("alipay order vo : {}", vo);

		Long transId = transactionService
				.createTransactionFromBillingData(bill);

		PaymentForm result = generateForwardViewData(vo, transId);
		Assert.isNotNull(result);

		logger.debug("forwared view data [result : {}]", result);
		return result;
	}

	protected AlipayOrderVO convert(BillingData data, String partner) {
		Assert.isNotNull(data, "billing data cannot be null");
		Assert.isNotNull(partner);
		logger.trace("convert alipay order vo...[data: {}, partnert = {}]",
				data, partner);

		AlipayOrderVO vo = new AlipayOrderVO();
		vo.setOrderBody(data.getSubject());

		Assert.isNotNull(data.getPrice());
		Assert.isTrue(data.getPrice().compareTo(BigDecimal.ZERO) > 0,
				"paid amount must be greater than zero");
		BigDecimal orderFee = data.getPrice()
				.multiply(BigDecimal.valueOf(data.getQuantity()))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		vo.setOrderFee(orderFee);
		vo.setBizNumber(data.getBizNo());
		vo.setOrderNumber(ParamUtils.getOrderNo(partner));
		data.setOrderNo(vo.getOrderNumber());
		vo.setOrderSubject(data.getSubject());
		vo.setContextUrl(data.getContextPath());
		// vo.setOrderDetailURL("http://test-cloud.anyi-tech.com:8080/pa/others/mobile/#!/anyi/ZHONGJING_CHANNEL/list?header=0");

		logger.trace("converted [alipay order vo : {}]", vo);
		return vo;
	}

	private PaymentForm generateForwardViewData(AlipayOrderVO orderVO,
												Long transId) throws Exception {
		Assert.isNotNull(orderVO);
		logger.trace("generate forward view date [orderVO : {}]", orderVO);

		AlipayConfig config = orderVO.getConfig();
		Assert.isNotNull(config);
		Assert.isNotNull(config.getPartner(), "config partener is null");
		Assert.isNotNull(config.getGateway(), "config gateway is null");
		Assert.isNotNull(config.getKey(), "config secretKey is null");

		Map<String, String> params = new HashMap<String, String>();

		if (config.isWapPayment()) {
			params.put("service", SERVICE_ALIPAY_WAP_GATEWAY);
		} else {
			params.put("service", SERVICE_ALIPAY_GATEWAY);
			params.put("exter_invoke_ip", "127.0.0.1");
			params.put("anti_phishing_key", "");
		}

		params.put("seller_id", config.getSellerId());
		params.put("_input_charset", AlipayConstant.INPUT_CHARSET);
		params.put("partner", config.getPartner());
		params.put("payment_type", "1");
		String notificationURL = orderVO.getContextUrl()
				+ "/api/payment/alipay/notifications";
		params.put("notify_url", notificationURL);

		// only for test
		// params.put("notify_url", "http://requestb.in/skkgixsk");
		params.put(
				"return_url",
				orderVO.getContextUrl()
						.concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH).concat("?transId=")
						.concat(transId.toString()));

		params.put("out_trade_no", orderVO.getOrderNumber());
		params.put("subject", orderVO.getOrderSubject());
		params.put("body", orderVO.getOrderBody());

		BigDecimal orderFee = orderVO.getOrderFee();
		Assert.isNotNull(orderFee);
		params.put("total_fee", orderFee.toString());
		params.put("show_url", config.getShowUrl());
		params.put("sign_type", AlipayConstant.SIGN_TYPE);
		params.put(
				"sign",
				AlipayHelper.md5Sign(params, config.getGateway(),
						config.getKey()));

		String url = config.getGateway() + "?_input_charset="
				+ AlipayConstant.INPUT_CHARSET;

		logger.trace("built parameter map : {}",
				AlipayHelper.getMapContent(params));
		PaymentForm viewData = new PaymentForm();

		viewData.setForwardType(FrontPageForwardType.POST);
		viewData.setForwardURL(PaymentConstant.GENERAL_POST_FORM_FORWARD_URL);
		viewData.setEndPoint(url);
		viewData.setParams(params);
		viewData.setTransactionId(transId);

		logger.trace("[forward view data : {} ]", viewData);
		return viewData;
	}
}
