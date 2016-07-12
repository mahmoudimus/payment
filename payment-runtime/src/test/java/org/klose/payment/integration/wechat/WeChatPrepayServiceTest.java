package org.klose.payment.integration.wechat;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.klose.payment.AbstractPaymentTest;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.util.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class WeChatPrepayServiceTest extends AbstractPaymentTest {
    private final static Logger logger = LoggerFactory
            .getLogger(WeChatPrepayServiceTest.class);
    private WechatPrepayData prepayData;

    @Before
    public void prepare() {
        prepayData = new WechatPrepayData();
        prepayData.setAppId("wx9ab9b4105f111a85");
        final String mchId = "1289029101";
        prepayData.setMchId(mchId);
        prepayData.setMchSecret("Ebaotech123Ebaotech123Ebaotech12");
        prepayData.setOpenId("oh_rtvwC_zUkSPNkarw3aJRL0WmQ");
        prepayData.setProductId("100000");
        prepayData.setTradeType(WeChatConstant.WEIXIN_TRADETYPE_JSAPI);
        prepayData.setProductName("Test Policy");
        prepayData.setOrderNo(ParamUtils.getOrderNo(mchId));
        prepayData.setTotal_fee(BigDecimal.valueOf(0.01));
    }

    @Test
    public void getPrepay() throws Exception {
        Assert.assertNotNull(prepayData);
        WechatPrepayResponseDto responseDto =  WechatPrepayService.getPrepay(prepayData, "");
        Assert.assertNotNull(responseDto);
        Assert.assertEquals("SUCCESS", responseDto.getReturn_code());
        Assert.assertNotNull(responseDto.getPrepay_id());
        Assert.assertNotNull(responseDto.getSign());
        Assert.assertNotNull(responseDto.getAmount());
        Assert.assertTrue(responseDto.getAmount() > 0);
        logger.info("prepay response : {}", responseDto);
    }
}
