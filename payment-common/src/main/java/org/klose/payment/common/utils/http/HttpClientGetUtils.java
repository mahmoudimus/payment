package org.klose.payment.common.utils.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;

import java.io.IOException;

public class HttpClientGetUtils {

    private static CloseableHttpClient initHttpClient() {
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
                                           String encoding) throws IOException {
        HttpGet hg = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(HttpClientConstants.SOCKET_TIMEOUT)
                .setConnectTimeout(HttpClientConstants.CONNECT_TIMEOUT).build();// 设置请求和传输超时时�?
        hg.setConfig(requestConfig);
        hg.setHeader(HTTP.CONTENT_TYPE, contentType);
        return HttpUtils.executeHttpRequest(initHttpClient(), hg, encoding);

    }


}
