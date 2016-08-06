package org.klose.payment.integration.bill99.constant;

public interface Bill99Constant {

    String INPUT_CHARSET = "1";
    String VERSION = "v2.0";
    String LANGUAGE = "1";
    String PAY_TYPE = "00";
    String SIGN_TYPE = "4";

    String[] REQUEST_PARAMETERS = new String[]{
            "inputCharset",
            "pageUrl",
            "bgUrl",
            "version",
            "language",
            "signType",
            "merchantAcctId",
            "payerName",
            "payerContactType",
            "payerContact",
            "payerIdType",
            "payerId",
            "payerIP",
            "orderId",
            "orderAmount",
            "orderTime",
            "orderTimestamp",
            "productName",
            "productNum",
            "productId",
            "productDesc",
            "ext1",
            "ext2",
            "payType",
            "bankId",
            "cardIssuer",
            "cardNum",
            "remitType",
            "remitCode",
            "redoFlag",
            "pid",
            "submitType",
            "orderTimeOut",
            "extDataType",
            "extDataContent"
    };

    String[] RETURN_PARAMETERS = new String[]{
            "merchantAcctId",
            "version",
            "language",
            "signType",
            "payType",
            "bankId",
            "orderId",
            "orderTime",
            "orderAmount",
            "bindCard",
            "bindMobile",
            "dealId",
            "bankDealId",
            "dealTime",
            "payAmount",
            "fee",
            "ext1",
            "ext2",
            "payResult",
            "errCode"
    };

    String PATH_NOTIFY_API = "/api/payment/bill99/notifications";

    String NOTIFY_RESPONSE_MSG =
            "<result>%d</result><redirecturl>%s</redirecturl>";

    String PARAM_KEY_RESULT= "payResult";

    String PARAM_KEY_DEAL_ID= "dealId";

    String PAYMENT_SUCCESS_CODE = "10";
}
