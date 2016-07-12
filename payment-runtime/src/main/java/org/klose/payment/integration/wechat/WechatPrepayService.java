package org.klose.payment.integration.wechat;


import org.apache.http.entity.StringEntity;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.klose.payment.common.exception.GeneralRuntimeException;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.http.HttpClientConstants;
import org.klose.payment.common.utils.http.HttpClientPostUtils;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.integration.wechat.dto.WechatPrepayRequestDto;
import org.klose.payment.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.util.DateUtil;
import org.klose.payment.util.ValidateCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

class WechatPrepayService {

    private final static BigDecimal DECIMAL_100 = new BigDecimal(100);
    private static Logger logger = LoggerFactory.getLogger(WechatPrepayService.class);

    static WechatPrepayResponseDto getPrepay(WechatPrepayData prepayData, String hostEndpoint) throws Exception {
        Assert.isNotNull(hostEndpoint);
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
        prepayDto.setNotify_url(hostEndpoint.concat(WeChatConstant.WECHAT_PREPAY_NOTIFY_URL));
//        prepayDto.setNotify_url("http://requestb.in/1izepjh1");
        prepayDto.setTrade_type(prepayData.getTradeType());
        prepayDto.setOpenid(prepayData.getOpenId() != null ? prepayData.getOpenId() : "");

        String wechatXml = prepayDto.getWechatXml();

        logger.info("WX Prepay Request: " + wechatXml);

        String content = HttpClientPostUtils.getHttpPostContentByEntity(
                WeChatConstant.CREATE_ORDER_URL,
                new StringEntity(wechatXml, StandardCharsets.UTF_8.name()),
                HttpClientConstants.headerValue_form, StandardCharsets.UTF_8.name());

        logger.info("WX Prepay Response: {} ", content.getBytes("UTF-8"));

        WechatPrepayResponseDto result = parsePrepayXml(content);
        result.setAmount(totalFee);
        result.setAppId(prepayData.getAppId());
        result.setSecurityKey(prepayData.getMchSecret());

        return result;
    }

    private static WechatPrepayResponseDto parsePrepayXml(String content) {

        WechatPrepayResponseDto prepayDto = new WechatPrepayResponseDto();
        Document document;
        try {
            document = DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            logger.error("prepay response cannot be parsed", e);
            throw new GeneralRuntimeException(e);
        }
        Element rootElement = document.getRootElement();
        if ("SUCCESS".equals(rootElement.elementText("return_code"))
                && "SUCCESS".equals(rootElement.elementText("result_code"))) {
            prepayDto.setPrepay_id(rootElement.elementText("prepay_id"));
            prepayDto.setSign(rootElement.elementText("sign"));
            prepayDto.setReturn_code("SUCCESS");
        } else {
            logger.error("Failed to create prepay order.The response is {}"
                    , content);
            throw new GeneralRuntimeException("failed to create prepay order, the response is {}" + content);
        }
        return prepayDto;
    }
}
