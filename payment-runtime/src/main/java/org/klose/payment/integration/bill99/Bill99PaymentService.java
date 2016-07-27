package org.klose.payment.integration.bill99;


import org.json.simple.JSONObject;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.LogUtils;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.klose.payment.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@Component("Bill99PaymentService")
public class Bill99PaymentService implements PaymentService {

    @Autowired
    private Bill99Helper helper;
    @Autowired
    private TransactionDataService transactionService;
    @Autowired
    private AccountService accountService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    final static String NOTIFY_RESPONSE_MSG = "<result>%d</result><redirecturl>%s?channelCode=%s&quotationNumber=%s</redirecturl>";

    final static String PARAM_KEY_RESULT = "payResult";

    final static String PARAM_KEY_DEAL_ID = "dealId";

    final static String PARAM_KEY_ORDER_ID = "orderId";

    final static String PARAM_KEY_CHANNEL_CODE = "ext1";

    final static String PARAM_KEY_PRODUCT_ID = "ext2";

    final static String PAYMENT_SUCCESS_CODE = "10";

    public JSONObject buildPaymentRequestData(String proxyId,  HttpServletRequest request) throws Exception {

        JSONObject requestData = new JSONObject();

//        String orderId = policy.getQuotationNumber();
//        String orderAmount = String.valueOf(policy.getFee("AGP").getAmount().multiply(new BigDecimal("100")).intValue());
//        String productId = policy.getProductId();
//
//        String redirectURL = PaymentUtility.getKuaiqianPaymentRedirectURL(request.getRequestURL().toString(), productId);
//        String callbackURL = PaymentUtility.getKuaiqianPaymentNotificationURL(request.getRequestURL().toString());
//
//        String productCode = policy.getProductName();
//        String channelCode = policy.getChannelCode();
//        Map<String, String> params = kqHelper.buildPaymentRequestParams(
//                orderId, orderAmount.toString(),
//                redirectURL,
//                callbackURL,
//                productCode, orderAmount.toString(), productCode, productId, channelCode);
//        String signature = kqHelper.sign(params);
//        requestData.putAll(params);
//        requestData.put("signMsg", signature);
//        requestData.put("paymentEndpoint", String.format("%s/recvMerchantInfoAction.htm", kqHelper.getGatewayURL()));
//
//        PaymentData paymentData = new PaymentData();
//        paymentData.setOrderNumber(orderId);
//        paymentData.setAmount(policy.getFee("AGP").getAmount());
//        paymentData.setSignString(signature);
//        paymentData.setStatus(PaymentData.STATUS_PROCEED);
//        paymentDataDao.save(paymentData);

        return requestData;
    }



    public Response handleCallback(String proxyId, Map params, String encoding) {


//        String paymentResult = ((String) params.get(PARAM_KEY_RESULT)).equals(PAYMENT_SUCCESS_CODE) ? "SUCCESS" : "FAIL";
//        String quoteNumber = (String) params.get(PARAM_KEY_ORDER_ID);
//        String payId = (String) params.get(PARAM_KEY_DEAL_ID);
//        String channelCode = (String) params.get(PARAM_KEY_CHANNEL_CODE);
//        String productId = (String) params.get(PARAM_KEY_PRODUCT_ID);
//
//        boolean verified = kqHelper.verify(params);
//
//        if (verified && quoteNumber != null) {
//
//            IndividualAgreement policy = insuranceAgreementService.findByQuotationNumber(quoteNumber);
//
//            try {
//                if ("SUCCESS".equals(paymentResult) && policy != null) {
//                    /*List<PrePay> prePays = policy.getChildrenByType(PrePay.class);
//					if(prePays.size() > 0){
//						PrePay prePay = prePays.get(0);
//						prePay.setStatus(PrePayStatus.PAYMENT_SUCCESS);
//						prePay.setReceiptDate(new Date());
//					}*/
//                    if (callBackService.processPaymentNotification(paymentResult, quoteNumber, payId)) {
//                        callBackService.processPolicy(quoteNumber, paymentResult, payId);
//                    }
//                }
//            } catch (Exception e) {
//                policy.setStatusCode(AgreementStatusCodeList.WAITING_MANUAL_HANDLING);
//                insuranceAgreementService.savePolicy(policy);
//            }
//        }
//
//        String return_msg = String.format(
//                NOTIFY_RESPONSE_MSG,
//                verified ? 1 : 0,
//                PaymentUtility.getKuaiqianPaymentRedirectURL((String) params.get("requestURL"), productId),
//                channelCode,
//                quoteNumber);
        String return_msg = "";
        logger.info("KQ Return MSG: " + return_msg);
        return Response.status(Response.Status.OK).entity(return_msg).type(MediaType.APPLICATION_XML_TYPE).build();
    }


    @Override
    public PaymentForm generatePaymentData(BillingData bill) {
        Assert.isNotNull(bill, "billing data cannot be null");
        logger.info("start generate alipay forward view data");
        logger.debug("[billing  : \n {} ]", bill);

        initConfig(bill);
        Long transId = transactionService
                .createTransactionFromBillingData(bill);

        PaymentForm paymentForm = new PaymentForm();
        paymentForm.setTransactionId(transId);
        paymentForm.setForwardType(FrontPageForwardType.POST);
        paymentForm.setForwardURL(PaymentConstant.GENERAL_POST_FORM_FORWARD_URL);

        String gateway = (String) bill.getConfigData().get("gateway");
        Assert.isNotNull(gateway);
        String gatewayUrl = gateway;
        paymentForm.setGatewayURL(gatewayUrl);

        paymentForm.setParams(this.generatePaymentForm(transId, bill));

        logger.info("finish generate payment form ");
        logger.debug("[generated payment form : \n {}]", paymentForm);

        return paymentForm;
    }

    private void initConfig(BillingData bill) {
        Assert.isNotNull(bill);
        String accountNo = bill.getAccountNo();
        Assert.isNotNull(accountNo);
        logger.debug("[accountNo = {} ]", accountNo);
        Map<String, Object> configMap = accountService.parseConfigData(accountNo);

        logger.debug("[ parsed config : {}", LogUtils.getMapContent(configMap));
        bill.addConfigData(configMap);
    }

    private Map<String, String> generatePaymentForm(Long transId, BillingData bill) {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("inputCharset", Bill99Helper.INPUT_CHARSET);
        // 服务器接收分账结果的后台地址
        String hostEndpoint = bill.getContextPath();
        Assert.isNotNull(hostEndpoint);
        final String PATH_NOTIFY_API = "/api/99bill/notifications";
        params.put("bgUrl", hostEndpoint.concat(PATH_NOTIFY_API));

        // 服务器回调前台地址
        String pageUrl = hostEndpoint.concat(PaymentConstant.GENERAL_RETURN_PROXY_PATH)
                .concat(transId.toString());

        params.put("version", Bill99Helper.VERSION);
        params.put("language", Bill99Helper.LANGUAGE);
        params.put("signType", Bill99Helper.SIGN_TYPE);
        params.put("merchantAcctId", (String) bill.getConfigData().get("merchantAcctId"));

        params.put("orderId", bill.getBizNo()); // quotation number
        BigDecimal amount = bill.getPrice().multiply(BigDecimal.valueOf(100));
        params.put("orderAmount", String.valueOf(amount.intValue()));
        params.put("orderTime", DateUtil.format(new Date(),
                DateUtil.DATE_TIME_FORMAT_COMPACT_S));

        //@TODO
//        params.put("productName", productName);
//        params.put("productNum", productNum);
//        params.put("productDesc", productDesc);
//
//        params.put("ext1", channelCode);
//        params.put("ext2", productId);

        params.put("payType", Bill99Helper.PAY_TYPE);
        return params;
    }
}
