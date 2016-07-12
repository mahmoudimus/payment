package org.klose.payment.integration.wechat.constant;

/**
 * Created by klose on 7/10/16.
 */
public interface WeChatConstant {
    String WEIXIN_TRADETYPE_JSAPI = "JSAPI";
    String WEIXIN_TRADETYPE_NATIVE = "NATIVE";

    String WEIXIN_JSAPI_FORWARD_URL = "/weixin.jsp";

    /**
     * 微信OpenID
     */
    String KEY_WEIXIN_OPENID = "openID";

    String KEY_WEIXIN_PRODUCT_ID = "productID";

    String KEY_WEIXIN_PREPAY_ID = "prepayID";

    /**
     * 微信公众号AppID
     */
    String KEY_WEIXIN_APP_ID = "appID";

    String KEY_WEIXIN_APP_SECRET = "appSecret";

    String KEY_WEIXIN_MCH_ID = "mchID";

    String KEY_WEIXIN_SECURITY = "securityKey";

    String CREATE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    String WECHAT_PREPAY_NOTIFY_URL = "/api/payment/wechat/callback";
}
