package org.klose.payment.common.utils.http;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.klose.payment.common.exception.GeneralRuntimeException;
import org.klose.payment.common.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class HttpsClienetPostUtils {

    private static Logger log = LoggerFactory
            .getLogger(HttpsClienetPostUtils.class);

    // 表示请求器是否已经做了初始化工作
    private boolean hasInit = false;

    // 连接超时时间，默认30秒
    private int socketTimeout = HttpClientConstants.socketTimeout;

    // 传输超时时间，默认30秒
    private int connectTimeout = HttpClientConstants.connectTimeout;

    // 请求器的配置
    private RequestConfig requestConfig;

    // HTTP请求器
    private CloseableHttpClient httpClient;

    // SSL 证书文件路径
    private String certLocalPath;
    // SSL 证书文件密码
    private String certPassword;

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

    public HttpsClienetPostUtils(String certPath, String certPWD)
            throws UnrecoverableKeyException, KeyManagementException,
            NoSuchAlgorithmException, KeyStoreException, IOException {
        Assert.isNotNull(certPath);
        certLocalPath = certPath;
        certPassword = certPWD;
        init();
    }

    private void init() throws IOException, KeyStoreException,
            UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyManagementException {
        Assert.isNotNull(certLocalPath);
        if (certPassword == null)
            certPassword = "";

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream instream = HttpClientPostUtils.class
                .getResourceAsStream(certLocalPath)) {
            keyStore.load(instream, certPassword.toCharArray());// 设置证书密码
        } catch (CertificateException | NoSuchAlgorithmException e) {
            log.error("exception occurs by loading certification", e);
            throw new GeneralRuntimeException(e);
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, certPassword.toCharArray()).build();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        // httpClient.getHostConfiguration().setProxy("127.0.0.1", 7001);
        // 根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout).build();
        hasInit = true;
    }

    /**
     * post https request
     *
     * @param url
     * @param postEntity
     * @param contentType
     * @param encoding
     * @return
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String sendPost(String url, StringEntity postEntity,
                           String contentType, String encoding) throws IOException,
            KeyStoreException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyManagementException {
        Assert.isNotNull(url);
        Assert.isNotNull(postEntity);
        Assert.isNotNull(contentType);
        Assert.isNotNull(encoding);
        if (!hasInit) {
            init();
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(HTTP.CONTENT_TYPE, contentType);
        httpPost.setEntity(postEntity);
        // 设置请求器的配置
        httpPost.setConfig(requestConfig);

        log.debug("executing request : {}", httpPost.getRequestLine());

        return HttpUtils.executeHttpRequest(httpClient, httpPost, encoding);
    }

    /**
     * post https request
     *
     * @param url
     * @param request
     * @param contentType
     * @param encoding
     * @return
     * @throws IOException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public String sendPost(String url, String request, String contentType,
                           String encoding) throws IOException, KeyStoreException,
            UnrecoverableKeyException, NoSuchAlgorithmException,
            KeyManagementException {
        Assert.isNotNull(request);
        Assert.isNotNull(encoding);
        return this.sendPost(url, new StringEntity(request, encoding),
                contentType, encoding);
    }
}
