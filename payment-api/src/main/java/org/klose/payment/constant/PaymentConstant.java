package org.klose.payment.constant;

import java.math.BigDecimal;

public final class PaymentConstant {

    public static final String CURRENCY_CNY = "CNY";
    public static final String CURRENCY_USD = "USD";

    public static final String GENERAL_POST_FORM_FORWARD_URL = "/paymentForm.jsp";
    public static final String GENERAL_RETURN_PROXY_PATH = "/api/payment/return";
    public static final String GENERAL_RETURN_PAGE_URL = "/paymentResult.jsp";

    public static final String KEY_PAYMENT_ENDPOINT = "endPoint";
    public static final String KEY_PAYMENT_RETURN_URL = "returnURL";
    public static final BigDecimal TESTING_PAY_AMOUNT = new BigDecimal("0.01");

    public static final String REDIRECT_PREFIX = "redirect:";

    private PaymentConstant() {
        throw new IllegalArgumentException("PaymentConstant can not be initialized");
    }
}
