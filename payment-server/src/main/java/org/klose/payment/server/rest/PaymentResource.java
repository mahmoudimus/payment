package org.klose.payment.server.rest;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.plugins.providers.html.View;
import org.klose.payment.server.api.PaymentProxy;
import org.klose.payment.server.bo.*;
import org.klose.payment.server.common.context.ApplicationContextUtils;
import org.klose.payment.server.common.utils.Assert;
import org.klose.payment.server.common.utils.http.HttpUtils;
import org.klose.payment.server.constant.FrontPageForwardType;
import org.klose.payment.server.constant.PaymentConstant;
import org.klose.payment.server.constant.PaymentType;
import org.klose.payment.server.rest.model.PaymentDto;
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

@Path("payment/")
@Component
public class PaymentResource {

    static Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    @Autowired
    private PaymentExtensionConfService paymentExtensionService;

    @Autowired
    private PaymentProxy paymentProxy;


    @GET
    @Path("/payment/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public PaymentResult queryPayment(@Context HttpServletResponse response,
                                      @Context HttpServletRequest request,
                                      @PathParam("transactionId") Long transactionId) {
        PaymentResult paymentResult = paymentProxy
                .queryPayment(transactionId);
        return paymentResult;
    }

    @POST
    @Path("/createPayment")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("text/html")
    public View createPayment(@Context HttpServletResponse response,
                              @Context HttpServletRequest request, @Form PaymentDto paymentDto) {
        final String DEFAULT_PREPAREBILLDATA_BEAN = "mockPaymentIntegtationService";

        final String DEFAULT_CALLBACKAGENT_BEAN = "mockCallbackService";

        Assert.isNotNull(paymentDto, "payment form is empty");
        Assert.isNotNull(paymentDto.getAccountNo(),
                "account no can not be null");
        Assert.isNotNull(paymentDto.getBizType(), "bizType can not be null");
        Assert.isNotNull(paymentDto.getBizNo(), "bizNo can not be null");

        logger.info("start create payment");
        logger.debug("[payment form : {}]", paymentDto);

        AccountInfo accountInfo = paymentProxy.getAccountbyNo(paymentDto
                .getAccountNo());
        Assert.isNotNull(accountInfo, "account info can not be null");
        Assert.isNotNull(accountInfo.getType(),
                "account payment type can not be null");
        Integer accountType = accountInfo.getType().getTypeId();

        String callbackAgentBean = null;
        String prepareBillDataBean = null;

        PaymentExtensionConf conf = paymentExtensionService
                .getPaymentExtensionByBizTypeAndAccountType(accountType,
                        paymentDto.getBizType());

        logger.trace("payment extension conf = {} ", conf);
        if (null == conf) {
            prepareBillDataBean = DEFAULT_PREPAREBILLDATA_BEAN;
            callbackAgentBean = DEFAULT_CALLBACKAGENT_BEAN;
        } else {
            prepareBillDataBean = conf.getPrepareBillingDataBean();
            callbackAgentBean = conf.getProcessPaymentCallbackBean();
        }

        PaymentIntegrationService paymentIntegrationService = ApplicationContextUtils
                .getBean(prepareBillDataBean, PaymentIntegrationService.class);
        Assert.isNotNull(paymentIntegrationService,
                "payment integration can not be inititalized by spring");

        BillingData data = paymentIntegrationService
                .prepareBillingData(paymentDto.getBizNo());
        data.setAccountNo(paymentDto.getAccountNo());
        data.setAccountType(accountType);
        data.setBizType(paymentDto.getBizType());
        data.setReturnURL(paymentDto.getReturnURL());
        data.setCallBackAgent(callbackAgentBean);
        data.setHostEndpoint(HttpUtils.getServletRootUrl(request));

        if (accountType.equals(PaymentType.WX_JSAPI.getTypeId())) {
            // special handle for we chat;
            if (request.getAttribute(PaymentConstant.KEY_WEIXIN_OPENID) != null)
                data.addExtData(PaymentConstant.KEY_WEIXIN_OPENID,
                        request.getAttribute(PaymentConstant.KEY_WEIXIN_OPENID));
        }

        ForwardViewData viewData;
        try {
            viewData = paymentProxy.createPayment(data);
        } catch (Exception ex) {
            logger.error("occured exption while creating payment", ex);
            throw new RuntimeException(ex.getMessage());
        }

        Assert.isNotNull(viewData, "ForwardViewData is null");

        logger.debug("[forward view data : {}]", viewData);

        paymentIntegrationService.saveTransactionId(
                paymentDto.getBizNo(), viewData.getTransactionId());

        if (FrontPageForwardType.REDIRECT.equals(viewData.getForwardType())) {
            try {
                response.sendRedirect(viewData.getForwardURL());
                return null;
            } catch (IOException ioEx) {
                throw new RuntimeException("redirect url fail. url="
                        + viewData.getForwardURL(), ioEx);
            }
        } else {


            View view = new View(viewData.getForwardURL());

            view.getModelMap().put("model", viewData.getParams());
            view.getModelMap().put("returnURL", viewData.getReturnURL());
            if (null != viewData.getEndPoint()) {
                view.getModelMap().put(PaymentConstant.KEY_PAYMENT_ENDPOINT,
                        viewData.getEndPoint());
            }

            return view;
        }
    }

}
