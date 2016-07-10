package org.klose.payment.common.utils.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.klose.payment.common.exception.GeneralRuntimeException;

public class HttpClientGetUtils {

	public static CloseableHttpClient initHttpClient() {
		return HttpClientBuilder.create().build();
	}

	/**
	 * Send and get response from the specified url, and convert to a string
	 * characters
	 * 
	 * @param url
	 * @param contentType
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public static String getHttpGetContent(String url, String contentType,
			String encoding) throws Exception {
		HttpGet hg = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(HttpClientConstants.socketTimeout)
				.setConnectTimeout(HttpClientConstants.connectTimeout).build();// 设置请求和传输超时时�?
		hg.setConfig(requestConfig);
		hg.setHeader(HTTP.CONTENT_TYPE, contentType);

		CloseableHttpClient client = initHttpClient();
		try {
			HttpResponse response = client.execute(hg);
			int status = response.getStatusLine().getStatusCode();
			if (status >= HttpStatus.SC_OK
					&& status < HttpStatus.SC_MULTIPLE_CHOICES) {
				return IOUtils.toString(response.getEntity().getContent(),
						encoding);
			} else
				throw new GeneralRuntimeException(
						HttpClientConstants.HTTP_RESPONSE_STATUS_ERROR.toString());
		} finally {
			client.close();
		}

	}
}
