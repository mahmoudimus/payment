package org.klose.payment.common.utils.http;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.klose.payment.common.exception.GeneralRuntimeException;
import org.klose.payment.common.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class HttpUtils {

    private static final String _255 = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static final Pattern pattern = Pattern.compile("^(?:" + _255 + "\\.){3}" + _255 + "$");

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        logger.debug("getIPAddress:IP address (x-forwarded-for): {}", ip);

        if (!hasIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            logger.debug("getIPAddress:IP address (Proxy-Client-IP): {}", ip);
        }
        if (!hasIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            logger.debug("getIPAddress:IP address (WL-Proxy-Client-IP): {}", ip);
        }
        boolean found = false;
        if (hasIP(ip)) {
            StringTokenizer tokenizer = new StringTokenizer(ip, ",");
            while (tokenizer.hasMoreTokens()) {
                ip = tokenizer.nextToken().trim();
                if (isIPv4Valid(ip) && !isIPv4Private(ip)) {
                    found = true;
                    break;
                }
            }
            logger.debug("getIPAddress:IP address (validIPv4): {}", ip);
        }
        if (!found) {
            ip = request.getRemoteAddr();
            logger.debug("getIPAddress:IP address (RemoteAddr): {}", ip);
        }
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
                logger.debug("getIPAddress:IP address (HostAddress): {}", ip);
            } catch (UnknownHostException e) {
                logger.error("getIPAddress:error.\n", e);
            }
        }

        logger.debug("getIPAddress:IP address: {}", ip);
        return ip;
    }

    public static long ipV4ToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16)
                + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
    }

    public static boolean isWechatBrowser(HttpServletRequest request) {
        String userAgent = StringUtils.lowerCase(request.getHeader("user-agent"));
        return StringUtils.indexOf(userAgent, "micromessenger") > 0;
    }

    private static boolean isIPv4Private(String ip) {
        long longIp = ipV4ToLong(ip);
        return (longIp >= ipV4ToLong("10.0.0.0") && longIp <= ipV4ToLong("10.255.255.255"))
                || (longIp >= ipV4ToLong("172.16.0.0") && longIp <= ipV4ToLong("172.31.255.255"))
                || (longIp >= ipV4ToLong("192.168.0.0") && longIp <= ipV4ToLong("192.168.255.255"));
    }

    private static boolean isIPv4Valid(String ip) {
        return pattern.matcher(ip).matches();
    }

    private static boolean hasIP(String ipStr) {
        return StringUtils.isNotBlank(ipStr) && !"unknown".equalsIgnoreCase(ipStr);
    }

    public static String getServletRootUrl(HttpServletRequest request) {
        Assert.isNotNull(request);
        // url: http://axatp.localhost:10080/pa_web/api/ebaopay/new
        String url = request.getRequestURL().toString();
        // uri: /pa_web/api/ebaopay/new
        String uri = request.getRequestURI();
        String root = url.substring(0, url.indexOf(uri));
        // rootContextUrl: http://axatp:localhost:10080/pa_web
        String rootContextUrl = root + request.getContextPath();
        logger.debug("root context url = {}", rootContextUrl);
        return rootContextUrl;
    }

    public static String executeHttpRequest(CloseableHttpClient httpClient, HttpRequestBase request,
                                            String encoding) throws IOException {
        try {
            HttpResponse response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status >= HttpStatus.SC_OK
                    && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                return IOUtils.toString(response.getEntity().getContent(),
                        encoding);
            } else {
                logger.error("the response status code {} is illegal", status);
                throw new GeneralRuntimeException(
                        HttpClientConstants.HTTP_RESPONSE_STATUS_ERROR.toString());
            }

        } finally {
            httpClient.close();
        }
    }

    public static StringEntity transformParameter(
            Map<String, List<String>> params, String encoding) {
        StringEntity entity = null;
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> nvps = new ArrayList<>();
            for (String key : params.keySet()) {
                for (String value : params.get(key))
                    nvps.add(new BasicNameValuePair(key, value));

            }
            try {
                entity = new UrlEncodedFormEntity(nvps,
                        encoding == null ? HttpClientConstants.DEFAULT_ENCODING : encoding);
            } catch (UnsupportedEncodingException e) {
                logger.error(
                        "transform post parameter to string entity throws UnsupportedEncodingException ",
                        e);
                throw new RuntimeException(e);
            }
        }

        return entity;
    }

    public static String concatParams(final String url,
                                      final Map<String, List<String>> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        if (params != null) {
            for (String key : params.keySet()) {
                for (String value : params.get(key)) {
                    sb.append(key);
                    sb.append("=");
                    sb.append(value);
                    sb.append("&");
                }
            }
            return sb.substring(0, sb.length() - 1);
        } else
            return url;
    }
}
