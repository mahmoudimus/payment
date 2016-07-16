package org.klose.payment.constant;

import java.math.BigDecimal;

public interface PaymentConstant {

    String CURRENCY_CNY = "CNY";
    String CURRENCY_USD = "USD";

    String GENERAL_POST_FORM_FORWARD_URL = "/paymentForm.jsp";
    String GENERAL_RETURN_PROXY_PATH = "/api/payment/return";
    String GENERAL_RETURN_PAGE_URL = "/paymentResult.jsp";

    String KEY_PAYMENT_ENDPOINT = "endPoint";
    String KEY_PAYMENT_RETURN_URL = "returnURL";
    BigDecimal TESTING_PAY_AMOUNT = new BigDecimal("0.01");

}
