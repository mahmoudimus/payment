package org.klose.payment.common.utils.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.klose.payment.common.exception.GeneralRuntimeException;

import java.util.*;

public class HttpClientPostUtils {

	public static CloseableHttpClient initHttpClient() {
		return HttpClientBuilder.create().build();
	}

	// public static HttpResponse getHttpPostResponse(String url,
	// String contentType) throws Exception {
	// return getHttpPostResponse(url, new ArrayList<NameValuePair>(),
	// contentType, StandardCharsets.UTF_8.name());
	// }
	//
	// public static HttpResponse getHttpPostResponse(String url,
	// Map<String, String> params, String contentType, String encoding)
	// throws Exception {
	// return getHttpPostResponse(url, generatePostParams(params),
	// contentType, encoding);
	// }
	//
	// public static HttpResponse getHttpPostResponse(String url, String params,
	// String contentType, String encoding) throws Exception {
	// return getHttpPostResponse(url, convertPOSTParametersToNVP(params),
	// contentType, encoding);
	// }

	public static String getHttpPostContent(String url, String contentType,
			String read_encoding) throws Exception {
		return getHttpPostContent(url, new ArrayList<NameValuePair>(),
				contentType, read_encoding);
	}

	/**
	 * post http request with http entity
	 * @param url
	 * @param entity
	 * @param contentType
	 * @param read_encoding
	 * @return
	 * @throws Exception
	 */
	public static String getHttpPostContentByEntity(String url,
			StringEntity entity, String contentType, String read_encoding)
			throws Exception {
		HttpPost post = new HttpPost(url);
		post.setHeader(HTTP.CONTENT_TYPE, contentType);
		post.setEntity(entity);

		CloseableHttpClient client = initHttpClient();
		try {
			HttpResponse response = client.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if (status >= HttpStatus.SC_OK
					&& status < HttpStatus.SC_MULTIPLE_CHOICES) {
				return IOUtils.toString(response.getEntity().getContent(),
						read_encoding);
			} else
				throw new RuntimeException(
						HttpClientConstants.HTTP_RESPONSE_STATUS_ERROR.toString());
		} finally {
			client.close();
		}

	}

	/**
	 * post http request with parameter
	 * @param url
	 * @param params
	 * @param contentType
	 * @param read_encoding
	 * @return
	 * @throws Exception
	 */
	public static String getHttpPostContent(String url, String params,
			String contentType, String read_encoding) throws Exception {
		return getHttpPostContent(url, convertPOSTParametersToNVP(params),
				contentType, read_encoding);
	}

	public static String getHttpPostContent(String url,
			Map<String, String> params, String contentType, String read_encoding)
			throws Exception {
		return getHttpPostContent(url, generatePostParams(params), contentType,
				read_encoding);
	}

	private static String getHttpPostContent(String url,
			List<NameValuePair> nvps, String contentType, String read_encoding)
			throws Exception {

		UrlEncodedFormEntity entity = null;
		if (nvps != null && nvps.size() > 0) {
			entity = new UrlEncodedFormEntity(nvps, read_encoding);
		}
		HttpPost post = new HttpPost(url);
		post.setHeader(HTTP.CONTENT_TYPE, contentType);
		if (entity != null) {
			post.setEntity(entity);
		}

		CloseableHttpClient client = initHttpClient();
		try {
			HttpResponse response = client.execute(post);
			int status = response.getStatusLine().getStatusCode();
			if (status >= HttpStatus.SC_OK
					&& status < HttpStatus.SC_MULTIPLE_CHOICES) {
				return IOUtils.toString(response.getEntity().getContent(),
						read_encoding);
			} else
				throw new GeneralRuntimeException(
						HttpClientConstants.HTTP_RESPONSE_STATUS_ERROR.toString());
		} finally {
			client.close();
		}

	}

	private static List<NameValuePair> generatePostParams(
			Map<String, String> params) {
		if (params == null) {
			return null;
		}

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Iterator<String> keyParam = params.keySet().iterator();
		while (keyParam.hasNext()) {
			String key = keyParam.next();
			NameValuePair pair = new BasicNameValuePair(key, params.get(key));
			nvps.add(pair);
		}

		return nvps;
	}

	/**
	 *
	 *
	 * @param POSTParameters
	 * @return
	 */
	private static Map<String, String> convertPOSTParametersToNVP(
			String POSTParameters) {
		Map<String, String> nvps = new LinkedHashMap<String, String>();
		for (String s : POSTParameters.split("&")) {
			String name = s.split("=")[0];
			String value = s.substring(s.indexOf("=") + 1);
			nvps.put(name, value);
		}
		return nvps;

	}
}
