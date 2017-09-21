package org.klose.payment.server.rest;

import org.klose.payment.api.PaymentProxy;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.http.HttpUtils;
import org.klose.payment.common.utils.wechat.WeChatOauth2Token;
import org.klose.payment.common.utils.wechat.WeChatUtil;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.server.rest.model.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_HTML_VALUE;

@RequestMapping(value = "/wechat")
@RestController
public class WeChatResource {
    private final static Logger logger = LoggerFactory
            .getLogger(WeChatResource.class);

    @Resource
    private PaymentResource payResource;

    @Resource
    private PaymentProxy paymentProxy;

    private final static String WECHAT_OAUTH_URL = "/api/wechat/createWeChatPayment";

    @RequestMapping(value = "/oauth", method = RequestMethod.POST)
    public String oauth(HttpServletRequest request, @RequestBody OrderDto orderDto)
            throws Exception {
        Assert.isNotNull(orderDto);
        logger.info("start oauth wechat");
        logger.debug("[orderDto : {}]", orderDto);

        String accountNo = orderDto.getAccountNo();
        Assert.isNotNull(accountNo);

        Map<String, Object> config = paymentProxy.parseConfig(accountNo);
        Assert.isNotNull(config);
        logger.trace("wechat config : {}", config);

        String bizNo = orderDto.getBizNo();
        Assert.isNotNull(bizNo);
        String bizType = orderDto.getBizType();
        String returnURL = orderDto.getReturnURL();

        StringBuilder builder = new StringBuilder();
        builder.append(HttpUtils.getServletRootUrl(request));
        builder.append(WECHAT_OAUTH_URL);
        builder.append("?bizNo=").append(bizNo);
        builder.append("&accountNo=").append(accountNo);

        if(bizType != null)
            builder.append("&bizType=").append(bizType);
        if(returnURL != null ) {
            returnURL = returnURL.replaceAll("#!", ";");
            builder.append("&returnURL=").append(returnURL);
        }


        String redirectUrl = WeChatUtil.getOAuthUrl((String) config.get(WeChatConstant.KEY_WEIXIN_APP_ID),
                builder.toString());

        logger.debug("[redirect url = {}", redirectUrl);
        return PaymentConstant.REDIRECT_PREFIX.concat(redirectUrl);
    }

    @RequestMapping(value = "/createWeChatPayment", method = RequestMethod.GET,
            consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = TEXT_HTML_VALUE)
    public ModelAndView createWeChatPayment(HttpServletResponse response,
                                            HttpServletRequest request) {
        Assert.isNotNull(request);
        String code = request.getParameter("code");
        Assert.isNotNull(code);

        logger.info("start create we chat payment ");
        logger.debug("[code = {}] ", code);

        String accountNo = request.getParameter("accountNo");
        Assert.isNotNull(accountNo);

        logger.debug("[accountNo = {}]", accountNo);

        Map<String, Object> config = paymentProxy.parseConfig(accountNo);
        Assert.isNotNull(config);
        logger.trace("wechat config : {}", config);

        WeChatOauth2Token token = WeChatUtil.getOauth2AccessToken(
                (String) config.get(WeChatConstant.KEY_WEIXIN_APP_ID),
                (String) config.get(WeChatConstant.KEY_WEIXIN_APP_SECRET), code);
        Assert.isNotNull(token);
        Assert.isNotNull(token.getOpenId());

        logger.trace("oauth token = {}", token);

        request.setAttribute(WeChatConstant.KEY_WEIXIN_OPENID,
                token.getOpenId());

        String bizNo = request.getParameter("bizNo");
        String bizType = request.getParameter("bizType");
        String returnURL = request.getParameter("returnURL");

        if(returnURL != null)
            returnURL = returnURL.replaceAll(";", "#!");

        logger.trace("bizNo = {}, bizType = {}, returnURL = {}", bizNo,
                bizType, returnURL);

        OrderDto orderDto = new OrderDto();
        orderDto.setAccountNo(accountNo);
        orderDto.setBizNo(bizNo);
        orderDto.setBizType(bizType);
        orderDto.setReturnURL(returnURL);

        logger.debug("[initialized orderDto = {} ]", orderDto);
        return payResource.createPayment(response, request, orderDto);
    }

}
