package org.klose.payment.common.utils.wechat;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.http.HttpClientConstants;
import org.klose.payment.common.utils.http.HttpClientGetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author klose.wu
 */
public class WeChatUtil {

    private final static Logger logger = LoggerFactory
            .getLogger(WeChatUtil.class);
    private final static String token_url = "https://api.weixin.qq.com/cgi-bin/token";
    private final static String oAuthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private final static String accessToken = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private final static String jsapi_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    private static Map<String, WeChatCGIToken> tokenMap = new ConcurrentHashMap<>();
    private static Map<String, WeChatJSAPITicket> ticketMap = new ConcurrentHashMap<>();

    public static WeChatCGIToken getAccessToken(String appId, String appSecret)
            throws Exception {
        Assert.isNotNull(appId, "appid is null");
        Assert.isNotNull(appSecret, "secretKey is null");
        logger.info("get wechat accessToken");
        logger.debug("[appId = {}]", appId);

        WeChatCGIToken token = tokenMap.get(appId);
        if (token == null || needRefreshToken(token)) {
            token = doGetAccessToken(appId, appSecret);
            tokenMap.put(appId, token);
        }

        logger.debug("[wechat cgi-toke = {}]", token);
        return token;
    }

    @SuppressWarnings("uncheck")
    private static WeChatCGIToken doGetAccessToken(String appId,
                                                   String appSecret) {
        Assert.isNotNull(appId, "appId is null");
        Assert.isNotNull(appSecret, "appSecret is null");

        StringBuilder url = new StringBuilder();
        url.append(token_url).append("?");
        url.append("grant_type=client_credential");
        url.append("&appid=").append(appId);
        url.append("&secret=").append(appSecret);

        logger.trace("get cgi token url = {}", url.toString());

        String content;
        try {
            content = HttpClientGetUtils.getHttpGetContent(url.toString(),
                    HttpClientConstants.headerValue_form,
                    StandardCharsets.UTF_8.name());

            Assert.isNotNull(content);

            logger.trace("response of get cgi token : {}", content);

            JSONObject o = (JSONObject) new JSONParser().parse(content);
            if (o.get("errcode") != null) {
                logger.error(
                        "error occurs by get wechat access token. errMsg : {}",
                        o.get("errmsg"));
                throw new RuntimeException(
                        "error occurs by get wechat access token. errMsg :"
                                + o.get("errmsg"));
            } else {
                String token = (String) o.get("access_token");
                Long expiredIn = (Long) o.get("expires_in");
                WeChatCGIToken accessToken = new WeChatCGIToken();
                accessToken.setToken(token);
                accessToken.setExpiredTime((new Date().getTime() / 1000)
                        + expiredIn);

                logger.trace("cgi token : {}" + accessToken);

                return accessToken;
            }
        } catch (Exception e) {
            logger.error("exception occurs by get wechat access token", e);
            throw new RuntimeException(e);
        }

    }

    private static boolean needRefreshToken(WeChatCGIToken token) {
        Assert.isNotNull(token);
        Assert.isNotNull(token.getExpiredTime());
        final Integer slot = 300; // 到期5分钟前刷新
        return (new Date().getTime() / 1000) + slot > token.getExpiredTime();
    }

    @SuppressWarnings("uncheck")
    public static WeChatJSAPITicket getJsapiTicket(String appId, String cgiToken) {
        Assert.isNotNull(appId);
        Assert.isNotNull(cgiToken);
        logger.info("start get js api ticket");
        logger.debug("[appId = {}, cgiToken = {} ]", appId, cgiToken);

        WeChatJSAPITicket ticket = ticketMap.get(appId);
        if (ticket == null || needRefreshTicket(ticket)) {
            ticket = doGetJsapiTicket(cgiToken);
            ticketMap.put(appId, ticket);
        }

        Assert.isNotNull(ticket);
        logger.info("end get js api ticket");
        logger.debug("[jsApiTicket = {} ]", ticket);

        return ticket;
    }

    private static WeChatJSAPITicket doGetJsapiTicket(String cgiToken) {
        StringBuilder url = new StringBuilder();
        url.append(jsapi_ticket + "?");
        url.append("access_token=").append(cgiToken);
        url.append("&type=jsapi");

        logger.trace("url of get js api ticket = {}", url.toString());

        String content;
        try {
            content = HttpClientGetUtils.getHttpGetContent(url.toString(),
                    HttpClientConstants.headerValue_form,
                    StandardCharsets.UTF_8.name());
            Assert.isNotNull(content);

            logger.trace("response of get js api ticket : {}", content);

            JSONObject o = (JSONObject) new JSONParser().parse(content);
            Long errcode = (Long) o.get("errcode");
            if (errcode > 0) {
                logger.error(
                        "error occurs by get wechat access token. errMsg : {}",
                        o.get("errmsg"));
                throw new RuntimeException(
                        "error occurs by get wechat access token. errMsg :"
                                + o.get("errmsg"));
            } else {
                String ticket = (String) o.get("ticket");
                Long expiredIn = (Long) o.get("expires_in");
                WeChatJSAPITicket jsAPITicket = new WeChatJSAPITicket();
                jsAPITicket.setTicket(ticket);
                jsAPITicket.setExpiredTime((new Date().getTime() / 1000)
                        + expiredIn);

                logger.trace("parsed js api ticket : {}", jsAPITicket);

                return jsAPITicket;
            }
        } catch (Exception e) {
            logger.error("exception occurs by get wechat js api ticket", e);
            throw new RuntimeException(
                    "exception occurs by get wechat js api ticket", e);
        }
    }

    private static boolean needRefreshTicket(WeChatJSAPITicket ticket) {
        Assert.isNotNull(ticket);
        Assert.isNotNull(ticket.getExpiredTime());
        final Integer slot = 300; // 到期5分钟前刷新
        return (new Date().getTime() / 1000) + slot > ticket.getExpiredTime();
    }

    @SuppressWarnings("uncheck")
    public static String getOAuthUrl(String appId, String redirectUrl) {
        Assert.isNotNull(appId);
        Assert.isNotNull(redirectUrl);

        logger.info("start generate oauth url");
        logger.debug("[appId = {}, redirectUrl = {}]", appId, redirectUrl);

        StringBuilder oauthUrl = new StringBuilder();

        oauthUrl.append(oAuthUrl + "?");
        oauthUrl.append("appid=").append(appId);
        try {
            oauthUrl.append("&redirect_uri=").append(
                    URLEncoder.encode((redirectUrl), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("exception occurs by encoding redirect_url", e);
            throw new RuntimeException(
                    "exception occurs by encoding redirect_url", e);
        }
        oauthUrl.append("&response_type=code");
        oauthUrl.append("&scope=snsapi_userinfo");
        oauthUrl.append("&state=1#wechat_redirect");

        logger.info("finish generate oauth url ");
        logger.debug("[result url = {}]", oauthUrl.toString());
        return oauthUrl.toString();
    }

    /**
     * 获取网页授权凭证
     *
     * @param appId     公众账号的唯一标识
     * @param appSecret 公众账号的密钥
     * @param code
     * @return WeixinAouth2Token
     */
    public static WeChatOauth2Token getOauth2AccessToken(String appId,
                                                         String appSecret, String code) {
        Assert.isNotNull(appId);
        Assert.isNotNull(appSecret);
        Assert.isNotNull(code);

        logger.info("start get oauth token ");
        logger.debug("[appId= {}, code = {} ]", appId, code);

        WeChatOauth2Token wat;

        if (!"authdeny".equals(code)) { // 拼接请求地址
            String requestUrl = accessToken
                    + "?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
            requestUrl = requestUrl.replace("APPID", appId);
            requestUrl = requestUrl.replace("SECRET", appSecret);
            requestUrl = requestUrl.replace("CODE", code);
            logger.trace("get oauth token url = {}", requestUrl);

            // 获取网页授权凭证
            String content;
            try {
                content = HttpClientGetUtils.getHttpGetContent(
                        requestUrl,
                        HttpClientConstants.headerValue_form,
                        StandardCharsets.UTF_8.name());
                Assert.isNotNull(content);
                logger.trace("response of get oauth token : {}", content);

                JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
                logger.trace("json object from response : {}", jsonObject);

                if (jsonObject.get("errcode") != null) {
                    logger.error("获取网页授权凭证失败 errmsg: "
                            + jsonObject.get("errmsg"));
                    throw new RuntimeException("获取网页授权凭证失败 errmsg: "
                            + jsonObject.get("errmsg"));
                } else {
                    wat = new WeChatOauth2Token();
                    wat.setAccessToken((String) jsonObject.get("access_token"));
                    wat.setExpiresIn(((Long)jsonObject.get("expires_in")).intValue());
                    wat.setRefreshToken((String) jsonObject.get("refresh_token"));
                    wat.setOpenId((String) jsonObject.get("openid"));
                    wat.setScope((String) jsonObject.get("scope"));

                    logger.info("finish get oauth token");
                    logger.debug(" [oauth token: {} ]", wat);
                    return wat;
                }
            } catch (Exception e) {
                logger.error("获取网页授权发生异常 ", e);
                throw new RuntimeException("获取网页授权发生异常 ", e);
            }
        } else {
            logger.error("not allowed authorzation, code = {}", code);
            throw new RuntimeException("not allowed authorzation, code = "
                    + code);
        }
    }
}
