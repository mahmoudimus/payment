package org.klose.payment.common.utils.http;

import java.nio.charset.StandardCharsets;

public interface HttpClientConstants {

	String HEADER_VALUE_FORM = "application/x-www-form-urlencoded; charset=utf-8";

	String CONTENT_TYPE_XML = "text/xml";

	Integer SOCKET_TIMEOUT = 30000;
	Integer CONNECT_TIMEOUT = 30000;
	Long HTTP_RESPONSE_STATUS_ERROR = 71000L;

	String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
	String SSL_KEY_STORE_TYPE_JKS = "jks";
	String SSL_KEY_STORE_TYPE_PKCS = "PKCS12";
	String SSL_PROTOCOL = "TLSv1";
	
}
