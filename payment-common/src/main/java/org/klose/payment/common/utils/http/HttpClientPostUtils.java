package org.klose.payment.common.utils.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpClientPostUtils {

    private static CloseableHttpClient initHttpClient() {
        return HttpClientBuilder.create().build();
    }

    public static String getHttpPostContent(String url, String contentType,
                                            String read_encoding) throws IOException {
        return getHttpPostContent(url, new ArrayList<NameValuePair>(),
                contentType, read_encoding);
    }

    public static String getHttpPostContentByEntity(String url,
                                                    StringEntity entity, String contentType, String read_encoding) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader(HTTP.CONTENT_TYPE, contentType);
        post.setEntity(entity);
        return HttpUtils.executeHttpRequest(initHttpClient(), post,
                read_encoding);
    }

    public static String getHttpPostContent(String url, String params,
                                            String contentType, String read_encoding) throws IOException {
        return getHttpPostContent(url, convertPOSTParametersToNVP(params),
                contentType, read_encoding);
    }

    private static String getHttpPostContent(String url,
                                            Map<String, String> params, String contentType, String read_encoding) throws IOException {
        return getHttpPostContent(url, generatePostParams(params), contentType,
                read_encoding);
    }

    private static String getHttpPostContent(String url,
                                             List<NameValuePair> nvps, String contentType, String read_encoding) throws IOException {

        UrlEncodedFormEntity entity = null;
        if (nvps != null && nvps.size() > 0)
            entity = new UrlEncodedFormEntity(nvps, read_encoding);

        return getHttpPostContentByEntity(url, entity, contentType, read_encoding);
    }

    private static List<NameValuePair> generatePostParams(
            Map<String, String> params) {
        if (params == null)
            return null;

        List<NameValuePair> nvps = new ArrayList<>();
        for (String key : params.keySet()) {
            NameValuePair pair = new BasicNameValuePair(key, params.get(key));
            nvps.add(pair);
        }

        return nvps;
    }

    /**
     * @param POSTParameters
     * @return
     */
    private static Map<String, String> convertPOSTParametersToNVP(
            String POSTParameters) {
        Map<String, String> nvps = new LinkedHashMap<>();
        for (String s : POSTParameters.split("&")) {
            String name = s.split("=")[0];
            String value = s.substring(s.indexOf("=") + 1);
            nvps.put(name, value);
        }
        return nvps;

    }
}
