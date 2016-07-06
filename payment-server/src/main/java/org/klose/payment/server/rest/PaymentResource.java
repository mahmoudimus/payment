package org.klose.payment.server.rest;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.plugins.providers.html.View;
import org.klose.payment.server.api.PaymentProxy;
import org.klose.payment.server.bo.*;
import org.klose.payment.server.common.context.ApplicationContextUtils;
import org.klose.payment.server.common.utils.Assert;
import org.klose.payment.server.common.utils.http.HttpUtils;
import org.klose.payment.server.constant.FrontPageForwardType;
import org.klose.payment.server.rest.model.OrderDto;
import org.klose.payment.server.service.PaymentExtensionConfService;
import org.klose.payment.server.service.PaymentIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/payment")
@Component
public class PaymentResource {

    static Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    @Autowired
    private PaymentExtensionConfService paymentExtensionService;

    @Autowired
    private PaymentProxy paymentProxy;


    @GET
    @Path("/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentResult queryPayment(@Context HttpServletResponse response,
                                      @Context HttpServletRequest request,
                                      @PathParam("transactionId") Long transactionId) {
        PaymentResult paymentResult = paymentProxy
                .queryPayment(transactionId);
        return paymentResult;
    }

    /**
     * 1. from order generate billing
     * 2. from billing generate payment form
     * 3. send/redirect payment form
     *
     * @param response
     * @param request
     * @param orderDto
     * @return
     */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/html")
    public View createPayment(@Context HttpServletResponse response,
                              @Context HttpServletRequest request, @Form OrderDto orderDto) {
        Assert.isNotNull(orderDto);
        Assert.isNotNull(orderDto.getAccountNo());
        Assert.isNotNull(orderDto.getBizType());
        Assert.isNotNull(orderDto.getBizNo());
        logger.info("start create payment");
        logger.debug("[payment form : {}]", orderDto);

        BillingData billing = this.prepareBilling(request, orderDto);
        PaymentForm paymentForm = this.generatePaymenetFormData(billing);
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
            return this.createPaymentView(paymentForm);
    }

    private BillingData prepareBilling(HttpServletRequest request, OrderDto orderDto) {
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
        data.setAccountNo(orderDto.getAccountNo());
        data.setAccountType(accountInfo.getType().getTypeId());
        data.setBizType(orderDto.getBizType());
        data.setReturnURL(orderDto.getReturnURL());
        data.setPrepareService(prepareBillDataBean);
        data.setCallBackAgent(callbackAgentBean);
        // relative path support
        data.setHostEndpoint(HttpUtils.getServletRootUrl(request));

        logger.debug("prepared billing data : \n {}", data);
        return data;
    }

    private PaymentForm generatePaymenetFormData(BillingData data) {
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

    private View createPaymentView(PaymentForm paymentForm) {
        Assert.isNotNull(paymentForm);
        Assert.isNotNull(paymentForm.getForwardURL());
        Assert.isNotNull(paymentForm.getParams());
        Assert.isNotNull(paymentForm.getReturnURL());

        logger.debug("forward view data : {} \n", paymentForm);

        View view = new View(paymentForm.getForwardURL());
        //@TODO rename
        view.getModelMap().put("model", paymentForm.getParams());
        view.getModelMap().put("returnURL", paymentForm.getReturnURL());

        logger.debug("payment view data : {} \n", view);
        return view;
    }
}
