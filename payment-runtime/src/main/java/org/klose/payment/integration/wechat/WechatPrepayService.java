package org.klose.payment.integration.wechat;


import org.apache.http.entity.StringEntity;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.klose.payment.common.utils.http.HttpClientConstants;
import org.klose.payment.common.utils.http.HttpClientPostUtils;
import org.klose.payment.integration.wechat.dto.WechatPrepayRequestDto;
import org.klose.payment.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.util.DateUtil;
import org.klose.payment.util.ValidateCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class WechatPrepayService {

	final static String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	final static String WECHAT_PREPAY_NOTIFY_URL = "/api/paymentservice/wechat/callback";
	
	final static BigDecimal DECIMAL_100 = new BigDecimal(100);
	
	private static Logger logger = LoggerFactory.getLogger(WechatPrepayService.class);
	
	public static WechatPrepayResponseDto getPrepay(WechatPrepayData prepayData, String hostEndpoint) throws Exception {
			
		WechatPrepayRequestDto prepayDto = new WechatPrepayRequestDto(
				prepayData.getAppId(), prepayData.getMchId(), prepayData.getMchSecret());

		prepayDto.setNonce_str(DateUtil.format(new Date(),
				DateUtil.DATE_KEY_STR) + ValidateCode.randomCode(10));
		prepayDto.setBody(prepayData.getProductName());
		prepayDto.setProductId(prepayData.getProductId());
		prepayDto.setOut_trade_no(prepayData.getOrderNo());
		
		Integer totalFee = prepayData.getTotal_fee().multiply(DECIMAL_100).intValue();
		prepayDto.setTotal_fee(totalFee);				
		
		prepayDto.setSpbill_create_ip("127.0.0.1");
		prepayDto.setNotify_url(hostEndpoint.concat(WECHAT_PREPAY_NOTIFY_URL));
		prepayDto.setTrade_type(prepayData.getTradeType());
		prepayDto.setOpenid(prepayData.getOpenId() != null ? prepayData.getOpenId() : "");

		String wechatXml = prepayDto.getWechatXml();
		
		logger.info("WX Prepay Request: " + wechatXml);

		String content = HttpClientPostUtils.getHttpPostContentByEntity(
				createOrderURL, 
				new StringEntity(wechatXml,	StandardCharsets.UTF_8.name()),
				HttpClientConstants.headerValue_form, StandardCharsets.UTF_8.name());

		logger.info("WX Prepay Response: " + content.getBytes("UTF-8"));

		WechatPrepayResponseDto result = parsePrepayXml(content);
		result.setAmount(totalFee);
		result.setAppId(prepayData.getAppId());
		result.setSecurityKey(prepayData.getMchSecret());
		
		return result;
	}

	private static WechatPrepayResponseDto parsePrepayXml(String content)
			throws Exception {

		WechatPrepayResponseDto prepayDto = new WechatPrepayResponseDto();
		Document document = DocumentHelper.parseText(content);

		Element rootElement = document.getRootElement();
		if ("SUCCESS".equals(rootElement.elementText("return_code"))
				&& "SUCCESS".equals(rootElement.elementText("result_code"))) {
			String prepay_id = rootElement.elementText("prepay_id");
			String code_url = rootElement.elementText("code_url");
			String sign = rootElement.elementText("sign");
			prepayDto.setPrepay_id(prepay_id);
			prepayDto.setCode_url(code_url);
			prepayDto.setSign(sign);
		} else {
			logger.error("Failed to create prepay order.The response is"
					+ content);
		}
		return prepayDto;
	}
}
