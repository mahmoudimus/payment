package org.klose.payment.constant;

public enum ErrorCode {
    ERR_PAYMENT_CONFIG_NOT_FOUND(102001L),
    ERR_PAYMENT_SECRET_KEY_NOT_FOUND(102002L),
    ERR_CALLBACK_SERVER_NO_RESPONSE(102003L),
    ERR_CALLBACK_ILLEGAL_REQUEST(102004L),
    ERR_PLAN_PERIOD_EQUALS_ZERO(102005L);

    private Long code = null;

    ErrorCode(Long code) {
        this.code = code;
    }

}
