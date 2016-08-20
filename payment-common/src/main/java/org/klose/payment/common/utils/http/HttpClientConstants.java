package org.klose.payment.common.utils.http;

public interface HttpClientConstants {
	
	String headerValue_form = "application/x-www-form-urlencoded; charset=utf-8";
	
	int socketTimeout = 30000;
	int connectTimeout = 30000;

	Long HTTP_RESPONSE_STATUS_ERROR = 71000L;
	
}
