package org.klose.payment.server.constant;

import java.math.BigDecimal;

public class PaymentConstant {
	
	public final static String CURRENCY_CNY = "CNY";
	
	public final static String CURRENCY_USD = "USD";
	
	public final static String WEIXIN_TRADETYPE_JSAPI = "JSAPI";
	
	public final static String WEIXIN_TRADETYPE_NATIVE = "NATIVE";
	
	/** 微信OpenID */
	public final static String KEY_WEIXIN_OPENID = "openID";
	
	public final static String KEY_WEIXIN_PRODUCT_ID = "productID";
	
	public final static String KEY_WEIXIN_PREPAY_ID = "prepayID";

	/** 微信公众号AppID */
	public final static String KEY_WEIXIN_APP_ID = "appID";
	
	public final static String KEY_WEIXIN_APP_SECRET = "appSecret";
	
	public final static String KEY_WEIXIN_MCH_ID = "mchID";
	
	public final static String KEY_WEIXIN_SECURITY = "securityKey";
	
	public final static String KEY_TRANSACTION_ID = "transactionId";
	
	public final static String KEY_ORDER_NO = "orderNumber";
	
	public final static String KEY_PAYMENT_NO = "paymentNo";
	
	public final static String KEY_POLICY_NO = "policyNo";
	
	public final static String KEY_PAY_CHANNEL = "payChannel";
	
	public final static String KEY_PAY_RESULT = "payResult";
	
	public final static String KEY_PAY_TIME = "payTime";
	
	public final static String KEY_PAY_ACCOUNT = "payAccount";
	
	public final static String KEY_PAY_AMOUNT = "payAmount";
	
	public final static String KEY_CCIC_PAYMENT_URL = "paymentUrl";
	
	public final static String KEY_CCIC_WECHAT_PAYMENT_URL = "wechatPaymentUrl";

	public final static String WEIXIN_JSAPI_FORWARD_URL = "/payment/weixin.jsp";
	
	public final static String GENERAL_POST_FORM_FORWARD_URL = "../../payment/ebaoFormPay.jsp";
	
	public final static String CCIC_GET_FORWARD_URL = "../../payment/ebaoCCICPay.jsp";
	
	public final static String GENERAL_RETURN_PROXY_PATH = "/api/paymentservice/return/";
	
	
	public final static String KEY_PAYMENT_ENDPOINT = "endPoint";
	
	public final static String KEY_PAYMENT_RETURN_URL = "returnURL";
	
	public final static String SUFFIX_RETURN_URL = "/%s";
	
	public final static BigDecimal TESTING_PAY_AMOUNT = new BigDecimal("0.01");

	/**页面回调URL参数,建议存放对象数组或集合类型*/
	public static final String RETURN_URL_PARAMS = "returnUrlParams";
	/** 机构代码 */
	public static final String DEPT_NO = "deptNo";
	/** 交易明细列表 */
	public static final String TRADE_LIST = "tradeList";
	/** 明细交易流水号 */
	public static final String TRADE_DETAIL_NO = "tradeDetailNo";
	/** 人员姓名 */
	public static final String PERSONNEL_NAME = "personnelName";
	/** 证件号码 */
	public static final String CERTIFICATE_NO = "certificateNo";
	/** 手机号 */
	public static final String CELL_PHONE = "cellPhone";
	/** 明细订单金额 */
	public static final String TRADE_DETAIL_AMOUNT = "tradeDetailAmount";
	/** 页面回调地址 */
	public static final String RETURN_PAGE_URL = "returnPageUrl";
	/** 服务器端回调地址 */
	public static final String NOTIFICATION_URL = "notificationUrl";
	/** 请求报文 */
	public static final String REQUEST_DOC = "requestDoc";
	/** 跳转页面URL */
	public static final String FORWARD_URL = "forwardUrl";
	/** 私钥别名 */
	public static final String PRIVATE_KEY_ALIAS = "privateKeyAlias";
	/** 私钥密码 */
	public static final String PRIVATE_KEY_PWD = "privateKeyPwd";
	/** 私钥路径 */
	public static final String PRIVATE_KEY_PATH = "privateKeyPath";
	/** OAuth重定向URL */
	public static final String OAUTH_REDIRECT_URL = "oauthRediredUrl";
	/** OAuth API地址 */
	public static final String OAUTH_API = "oauthAPI";
}
