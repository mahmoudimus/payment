package org.klose.payment.utils.wechat;

import junit.framework.Assert;
import org.junit.Test;
import org.klose.payment.common.utils.wechat.WeChatCGIToken;
import org.klose.payment.common.utils.wechat.WeChatJSAPITicket;
import org.klose.payment.common.utils.wechat.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by klose.wu on 7/12/16.
 */
public class WeChatUtilTest {
    private final static String APP_ID = "wx9ab9b4105f111a85";
    private final static String APP_SECRET = "311b6a14529fe083fdc60bd3ddebb479";
    private final static Logger logger = LoggerFactory.getLogger(WeChatUtilTest.class);

    @Test
    public void testGetCgiToken() throws Exception {
        WeChatCGIToken cgiToken = WeChatUtil.getAccessToken(APP_ID, APP_SECRET);
        Assert.assertNotNull(cgiToken);
        Assert.assertNotNull(cgiToken.getToken());
        logger.info("cgiToken : {} ", cgiToken);
    }

    @Test
    public void testGetJsApiTicket() throws Exception {
        WeChatCGIToken cgiToken = WeChatUtil.getAccessToken(APP_ID, APP_SECRET);
        WeChatJSAPITicket ticket = WeChatUtil.getJsapiTicket(APP_ID, cgiToken.getToken());
        Assert.assertNotNull(ticket);
        Assert.assertNotNull(ticket.getTicket());
        logger.info("jsApiTicket : {}", ticket);

    }

    @Test
    public void testGetOAuthUrl() throws Exception {
        String oauthUrl = WeChatUtil.getOAuthUrl(APP_ID, "http://weixindev.test.com/");
        Assert.assertNotNull(oauthUrl);
        logger.info("oauthUrl = {} ", oauthUrl);
    }
}
