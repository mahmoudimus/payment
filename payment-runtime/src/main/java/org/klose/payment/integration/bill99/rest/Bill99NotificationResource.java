package org.klose.payment.integration.bill99.rest;

import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.LogUtils;
import org.klose.payment.common.utils.http.HttpUtils;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.dao.TransactionDao;
import org.klose.payment.integration.bill99.Bill99Helper;
import org.klose.payment.integration.bill99.constant.Bill99Constant;
import org.klose.payment.po.TransactionPO;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.notification.ProcessNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Path("payment/bill99")
@Component
public class Bill99NotificationResource {

    @Autowired
    private TransactionDao dataDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProcessNotificationService notificationService;

    private final static Logger logger =
            LoggerFactory.getLogger(Bill99NotificationResource.class);


    @SuppressWarnings("unchecked")
    @POST
    @Path("/notifications")
    public Response processNotification(@Context HttpServletRequest request) {
        Map<String, String> params = parseRequest(request);
        logger.info("begin process 99bill notifications");
        String notifyData = LogUtils.getMapContent(params);
        logger.debug("[response params : {}", notifyData);

        Assert.isNotNull(params.get("ext1"));
        Long transId = Long.valueOf(params.get("ext1"));
        String publicKeyPath = getPublicKeyPath(transId);

        Assert.isNotNull(publicKeyPath);
        String return_msg;
        boolean verified = Bill99Helper.verify(params, publicKeyPath);


        if (verified) {
            String paymentResult = params.get(
                    Bill99Constant.PARAM_KEY_RESULT);
            Assert.isNotNull(paymentResult);

            if (paymentResult.equals(Bill99Constant.PAYMENT_SUCCESS_CODE)) {
                String payId = params.get(Bill99Constant.PARAM_KEY_DEAL_ID);
                Assert.isNotNull(payId);
                notificationService.handlePaymentCallback(transId,
                        notifyData, payId,
                        paymentResult, params);
            } else
                notificationService.handlePaymentCallback(transId,
                        notifyData, null, paymentResult, params);
        } else
            notificationService.handlePaymentCallback(transId,
                    notifyData, null, "verification failed", params);

        return_msg = String.format(
                Bill99Constant.NOTIFY_RESPONSE_MSG,
                verified ? 1 : 0,
                HttpUtils.getServletRootUrl(request).concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH).
                        concat("?transId=").concat(transId.toString()));
        logger.info("response to 99bill: " + return_msg);
        return Response.status(Response.Status.OK).entity(return_msg).
                type(MediaType.APPLICATION_XML_TYPE).build();
    }


    private Map<String, String> parseRequest(HttpServletRequest request) {
        Assert.isNotNull(request);
        Enumeration keys = request.getParameterNames();
        Map<String, String> params = new HashMap<>();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            params.put(key, request.getParameter(key));
        }

        params.put("requestURL", request.getRequestURL().toString());
        logger.debug("payment repsponse parameter map: {}",
                LogUtils.getMapContent(params));
        return params;
    }

    private String getPublicKeyPath(Long transId) {
        Assert.isNotNull(transId);
        Map<String, Object> configMap = getConfig(transId);
        Assert.isNotNull(configMap.get("publicKeyPath"));
        String publicKeyPath = (String) configMap.get("publicKeyPath");
        logger.trace("public key path = {}", publicKeyPath);
        return publicKeyPath;
    }

    private Map<String, Object> getConfig(Long transId) {
        Assert.isNotNull(transId, "transId is null");
        logger.trace("transId = {}", transId);
        TransactionPO po = dataDao.findOne(transId);
        Assert.isNotNull(po, "payment transaction is null");
        String accountNo = po.getAccountNo();
        Assert.isNotNull(accountNo,
                "accountNo in transactionPO is null");
        logger.trace("account PO : {}", po);
        Map<String, Object> config = accountService.parseConfigData(accountNo);
        Assert.isNotNull(config);
        logger.trace("account config : {}", LogUtils.getMapContent(config));
        return config;
    }
}
