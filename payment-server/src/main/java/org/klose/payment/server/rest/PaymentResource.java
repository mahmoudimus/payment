package org.klose.payment.server.rest;

import org.apache.commons.lang3.StringUtils;
import org.klose.payment.api.PaymentProxy;
import org.klose.payment.bo.*;
import org.klose.payment.common.context.ApplicationContextUtils;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.http.HttpUtils;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.constant.PaymentType;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.server.prepare.PaymentIntegrationService;
import org.klose.payment.server.rest.model.OrderDto;
import org.klose.payment.service.PaymentExtensionConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.klose.payment.constant.PaymentConstant.REDIRECT_PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping(value = "/payment")
@RestController
public class PaymentResource {

    private final static Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    @Resource
    private PaymentExtensionConfService paymentExtensionService;

    @Resource
    private PaymentProxy paymentProxy;


    @RequestMapping(value = "/{transactionId}", method = RequestMethod.GET)
    public PaymentResult queryPayment(@PathVariable("transactionId") Long transactionId) {
        return paymentProxy
                .queryPayment(transactionId);
    }

    /**
     * 1. from order generate billing
     * 2. from billing generate payment form
     * 3. send/redirect payment form
     *
     * @param response httpSeveletResponse
     * @param request  httpSeveletRequest
     * @param orderDto order form
     * @return payment form
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE)
    public Map<String, Object> createPayment(HttpServletResponse response,
                                             HttpServletRequest request, @RequestBody OrderDto orderDto) {
        Assert.isNotNull(orderDto);
        Assert.isNotNull(orderDto.getAccountNo());
        Assert.isNotNull(orderDto.getBizNo());
        logger.info("start create payment");
        logger.debug("[payment form : {}]", orderDto);

        BillingData billing = this.preparePayment(request, orderDto);
        PaymentForm paymentForm = this.generatePaymentForm(billing);
        Assert.isNotNull(paymentForm);
        Assert.isNotNull(paymentForm.getForwardType());

        PaymentIntegrationService paymentIntegrationService = ApplicationContextUtils
                .getBean(billing.getPrepareService(), PaymentIntegrationService.class);
        paymentIntegrationService.saveTransactionId(
                orderDto.getBizNo(), paymentForm.getTransactionId());


        if (FrontPageForwardType.REDIRECT.equals(paymentForm.getForwardType())) {
            redirectPaymentForm(response, paymentForm.getForwardURL());
            return null;
        } else
            return this.createPaymentView(paymentForm).getModelMap();
    }

    private BillingData preparePayment(HttpServletRequest request, OrderDto orderDto) {
        // init account info
        AccountInfo accountInfo = paymentProxy.getAccountbyNo(orderDto
                .getAccountNo());
        Assert.isNotNull(accountInfo);
        Assert.isNotNull(accountInfo.getType());

        PaymentExtensionConf conf = paymentExtensionService
                .getPaymentExtensionByBizTypeAndAccountType(
                        accountInfo.getType().getTypeId(),
                        orderDto.getBizType());

        logger.trace("payment extension conf = {} ", conf);

        final String DEFAULT_PREPARE_BEAN = "mockPaymentIntegtationService";
        final String DEFAULT_CALLBACK_BEAN = "mockCallbackService";
        String prepareBillDataBean = (conf != null && StringUtils.isEmpty(conf.getPrepareBillingDataBean())) ?
                conf.getPrepareBillingDataBean() : DEFAULT_PREPARE_BEAN;
        String callbackAgentBean = (conf != null && StringUtils.isEmpty(conf.getProcessPaymentCallbackBean())) ?
                conf.getProcessPaymentCallbackBean() : DEFAULT_CALLBACK_BEAN;

        PaymentIntegrationService paymentIntegrationService = ApplicationContextUtils
                .getBean(prepareBillDataBean, PaymentIntegrationService.class);
        Assert.isNotNull(paymentIntegrationService,
                "payment integration can not be inititalized by spring");

        BillingData data = paymentIntegrationService
                .prepareBillingData(orderDto.getBizNo());
        data.setBizType(orderDto.getBizType());
        data.setAccountNo(accountInfo.getAccountNo());
        data.setAccountType(accountInfo.getType().getTypeId());
        data.setPrepareService(prepareBillDataBean);
        data.setCallBackAgent(callbackAgentBean);

        String contextPath = HttpUtils.getServletRootUrl(request);
        data.setContextPath(contextPath);

        // relative path support
        String returnURL = orderDto.getReturnURL() == null ?
                PaymentConstant.GENERAL_RETURN_PAGE_URL : orderDto.getReturnURL();
        if (returnURL.startsWith("/"))
            returnURL = contextPath.concat(returnURL);

        data.setReturnURL(returnURL);

        //special handle for wechat js api
        if (accountInfo.getType().equals(PaymentType.WX_JSAPI)) {
            data.addExtData(WeChatConstant.KEY_WEIXIN_PRODUCT_ID, "test product");
            data.addExtData(WeChatConstant.KEY_WEIXIN_OPENID, request.getAttribute(WeChatConstant.KEY_WEIXIN_OPENID));
        }

        logger.debug("prepared billing data : \n {}", data);
        return data;
    }

    private PaymentForm generatePaymentForm(BillingData data) {
        Assert.isNotNull(data);
        logger.debug("[billingdata : {}", data);

        PaymentForm viewData;
        try {
            viewData = paymentProxy.createPayment(data);
        } catch (Exception ex) {
            logger.error("occured exption while creating payment", ex);
            throw new RuntimeException(ex.getMessage());
        }

        Assert.isNotNull(viewData, "PaymentForm is null");
        logger.debug("[forward view data : {}]", viewData);

        return viewData;
    }

    private void redirectPaymentForm(HttpServletResponse response, String redirectUrl) {
        Assert.isNotNull(response);
        Assert.isNotNull(redirectUrl);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException ioEx) {
            logger.error("{} exception error occurs by redirect {}",
                    ioEx.getMessage(), redirectUrl);
            throw new RuntimeException("redirect url fail. url="
                    + redirectUrl, ioEx);
        }
    }

    private ModelAndView createPaymentView(PaymentForm paymentForm) {
        Assert.isNotNull(paymentForm);
        Assert.isNotNull(paymentForm.getForwardURL());
        Assert.isNotNull(paymentForm.getParams());

        logger.debug("return url = {}", paymentForm.getReturnURL());

        logger.debug("forward view data : {} \n", paymentForm);

        //ModelAndView  view = new ModelAndView(paymentForm.getForwardURL());
        ModelAndView view = new ModelAndView("http://localhost");
        //@TODO rename
        view.getModelMap().put("model", paymentForm.getParams());
        view.getModelMap().put("returnURL", paymentForm.getReturnURL());
        view.getModelMap().put(PaymentConstant.KEY_PAYMENT_ENDPOINT,
                paymentForm.getGatewayURL());
        logger.debug("payment view data : {} \n", view);
        return view;
    }

    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public String redirect2ReturnUrl(
            HttpServletRequest request) throws URISyntaxException {
        String transIdVal = request.getParameter("transId");
        logger.debug("[transId = {}", transIdVal);
        Assert.isNotNull(transIdVal);
        String returnURL = paymentProxy.findReturnUrl(Long.valueOf(transIdVal)).concat("?transId=").concat(transIdVal);
        Assert.isNotNull(returnURL);
        logger.trace("returnURL = {}", returnURL);
        return REDIRECT_PREFIX.concat(returnURL);
    }

    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health() {
        return "payment service is running";
    }

}
