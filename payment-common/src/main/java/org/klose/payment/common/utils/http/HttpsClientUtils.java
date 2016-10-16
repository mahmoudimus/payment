package org.klose.payment.common.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

public class HttpsClientUtils {

    private static Logger logger = LoggerFactory
            .getLogger(HttpsClientUtils.class);

    // 表示请求器是否已经做了初始化工作
    private boolean hasInit = false;

    // 连接超时时间，默认30秒
    private int socketTimeout = HttpClientConstants.SOCKET_TIMEOUT;

    // 传输超时时间，默认30秒
    private int connectTimeout = HttpClientConstants.CONNECT_TIMEOUT;

    // 请求器的配置
    private RequestConfig requestConfig;

    // HTTP请求器
    private CloseableHttpClient client;

    // SSL 证书文件路径
    private String certLocalPath;
    // SSL 证书文件密码
    private String certPassword;

    //SSL 证书类型
    private String keyStoreType;

    private boolean selfSigned;

    /**
     * 设置连接超时时间
     *
     * @param socketTimeout 连接时长，默认10秒
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        resetRequestConfig();
    }

    /**
     * 设置传输超时时间
     *
     * @param connectTimeout 传输时长，默认30秒
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        resetRequestConfig();
    }

    private void resetRequestConfig() {
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
    }

    /**
     * 允许商户自己做更高级更复杂的请求器配置
     *
     * @param requestConfig 设置HttpsRequest的请求器配置
     */
    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    public String getCertLocalPath() {
        return certLocalPath;
    }

    public void setCertLocalPath(String certLocalPath) {
        this.certLocalPath = certLocalPath;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public boolean isSelfSigned() {
        return selfSigned;
    }

    public void setSelfSigned(boolean selfSigned) {
        this.selfSigned = selfSigned;
    }

    public HttpsClientUtils(final String certPath, final String certPWD, final String type, final Boolean selfSigned) {
        Assert.isNotNull(certPath);
        certLocalPath = certPath;
        certPassword = certPWD == null ? "" : certPWD;
        keyStoreType = type == null ? HttpClientConstants.SSL_KEY_STORE_TYPE_JKS : type;
        this.selfSigned = selfSigned == null ? true : selfSigned;
        init();
    }

    public HttpsClientUtils(final String certPath, final String certPWD) {
        Assert.isNotNull(certPath);
        certLocalPath = certPath;
        certPassword = certPWD == null ? "" : certPWD;
        keyStoreType = HttpClientConstants.SSL_KEY_STORE_TYPE_JKS;
        this.selfSigned = true;
        init();
    }

    private void init() {
        Assert.isNotNull(certLocalPath);
        Assert.isNotNull(certPassword);
        Assert.isNotNull(keyStoreType);
        logger.info("start initialize https client");
        logger.debug("cert path = {}, cert type = {}", certLocalPath, keyStoreType);

        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(
                    resourceLoader.getResource(
                            certLocalPath)
                            .getInputStream(), certPassword.toCharArray()); // 设置证书密码

            SSLContext sslcontext;
            if (selfSigned) {
                sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, certPassword.toCharArray())
                        .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
                        .build();
            } else
                sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, certPassword.toCharArray())
                        .build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, new String[]{HttpClientConstants.SSL_PROTOCOL}, null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            // client.getHostConfiguration().setProxy("127.0.0.1", 7001);
            // 根据默认超时限制初始化requestConfig
            requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout).build();
            hasInit = true;
        } catch (KeyManagementException e) {
            logger.error("init httpclienet throws KeyManagementException", e);
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("init httpclienet throws NoSuchAlgorithmException", e);
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            logger.error("init httpclienet throws KeyStoreException", e);
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            logger.error("init httpclienet throws CertificateException", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("init httpclienet throws IOException", e);
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            logger.error("init httpclienet throws UnrecoverableKeyException", e);
            throw new RuntimeException(e);
        }

        logger.info("finish intialize https client");
    }

    public String sendGet(final String url, final String encoding) {
        HttpResponse response = doGet(url);
        return this.getResponseContent(response, encoding);
    }

    public HttpResponse doGet(final String url) {
        Assert.isNotNull(url);
        logger.debug("url of httpGetRequest is " + url);
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig);
        return this.sendRequest(get);
    }

    public String sendGet(final String url,
                          final Map<String, List<String>> params, String encoding) {
        return sendGet(HttpUtils.concatParams(url, params), encoding);
    }

    public HttpResponse doGet(final String url,
                              final Map<String, List<String>> params) {
        return doGet(HttpUtils.concatParams(url, params));
    }

    public String sendPost(final String url, final String request, final String contentType,
                           final String encoding) {
        Assert.isNotNull(request);
        return this.sendPost(url, new StringEntity(request, encoding),
                contentType, encoding);
    }

    public String sendPost(final String url, final String encoding) {
        Assert.isNotNull(url);
        return sendPost(url, null, encoding);
    }

    public HttpResponse doPost(final String url) {
        Assert.isNotNull(url);
        StringEntity entity = null;
        return doPost(url, entity, null);
    }

    public String sendPost(final String url,
                           final Map<String, List<String>> params, final String encoding) {
        logger.debug("url = {}, params : {}, encoding = {}", url, LogUtils.getMapContent(params), encoding);
        return sendPost(url,
                HttpUtils.transformParameter(params, encoding), null,
                encoding);
    }

    public HttpResponse doPost(final String url,
                               final Map<String, List<String>> params, final String encoding) {
        return doPost(url,
                HttpUtils.transformParameter(params, encoding), null);
    }


    public String sendPost(final String url, final StringEntity entity,
                           final String contentType, final String encoding) {
        logger.debug("url of httpGetRequest = {}, StringEntity = {}, contentType = {}", url
                , entity, contentType);
        HttpResponse response = this.doPost(url, entity, contentType);
        return this.getResponseContent(response, encoding);
    }

    public HttpResponse doPost(final String url, final StringEntity entity,
                               final String contentType) {
        Assert.isNotNull(url);

        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        post.setHeader(HTTP.CONTENT_TYPE,
                contentType == null ? HttpClientConstants.HEADER_VALUE_FORM
                        : contentType);
        post.setEntity(entity);
        return sendRequest(post);
    }

    private HttpResponse sendRequest(HttpUriRequest request) {
        Assert.isNotNull(request);

        if (!hasInit) {
            init();
        }
        HttpResponse response;
        String result = null;

        logger.info("start send https request");
        logger.debug("request = {}", request);
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            logger.error("httpclient throw ClientProtocolException ", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("httpclient throw IOException", e);
            throw new RuntimeException(e);
        }

        logger.info("retrieve https response");
        logger.debug("the response of http request = {}", result);
        return response;
    }

    private String getResponseContent(final HttpResponse response,
                                      final String encoding) {
        Assert.isNotNull(response);
        String result;
        try {
            result = EntityUtils.toString(response.getEntity(),
                    encoding == null ? HttpClientConstants.DEFAULT_ENCODING : encoding);
        } catch (ParseException | IOException e) {
            logger.error(
                    "convert http response to StringEntity throw IOException",
                    e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
