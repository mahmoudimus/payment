package org.klose.payment.constant;

import java.math.BigDecimal;

public interface PaymentConstant {

    String CURRENCY_CNY = "CNY";
    String CURRENCY_USD = "USD";

    String GENERAL_POST_FORM_FORWARD_URL = "/paymentForm.jsp";
    String GENERAL_RETURN_PROXY_PATH = "/api/payment/return";
    String GENERAL_RETURN_PAGE_URL = "/paymentResult.jsp";

    String RETURN_PAGE_URL = "returnPageUrl";
    String KEY_PAYMENT_ENDPOINT = "endPoint";
    String KEY_PAYMENT_RETURN_URL = "returnURL";

    String SUFFIX_RETURN_URL = "/%s";
    BigDecimal TESTING_PAY_AMOUNT = new BigDecimal("0.01");

//	/**页面回调URL参数,建议存放对象数组或集合类型*/
//	 String RETURN_URL_PARAMS = "returnUrlParams";
//	/** 机构代码 */
//	 String DEPT_NO = "deptNo";
//	/** 交易明细列表 */
//	 String TRADE_LIST = "tradeList";
//	/** 明细交易流水号 */
//	 String TRADE_DETAIL_NO = "tradeDetailNo";
//	/** 人员姓名 */
//	 String PERSONNEL_NAME = "personnelName";
//	/** 证件号码 */
//	 String CERTIFICATE_NO = "certificateNo";
//	/** 手机号 */
//	 String CELL_PHONE = "cellPhone";
//	/** 明细订单金额 */
//	 String TRADE_DETAIL_AMOUNT = "tradeDetailAmount";
//	/** 页面回调地址 */
//	/** 服务器端回调地址 */
//	 String NOTIFICATION_URL = "notificationUrl";
//	/** 请求报文 */
//	 String REQUEST_DOC = "requestDoc";
//	/** 跳转页面URL */
//	 String FORWARD_URL = "forwardUrl";
//	/** 私钥别名 */
//	 String PRIVATE_KEY_ALIAS = "privateKeyAlias";
//	/** 私钥密码 */
//	 String PRIVATE_KEY_PWD = "privateKeyPwd";
//	/** 私钥路径 */
//	 String PRIVATE_KEY_PATH = "privateKeyPath";
//	/** OAuth重定向URL */
//	 String OAUTH_REDIRECT_URL = "oauthRediredUrl";
//	/** OAuth API地址 */
//	 String OAUTH_API = "oauthAPI";
}
