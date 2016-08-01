package org.klose.payment.integration.bill99.rest;

import org.klose.payment.integration.bill99.Bill99Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Path("bill99")
@Component
public class Bill99NotificationResource {
	private Bill99Helper helper;

	private final static Logger logger = LoggerFactory.getLogger(Bill99NotificationResource.class);

	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/callback")
	public Response processNotification(@Context HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Enumeration keys = request.getParameterNames();
		Map<String, Object> params = new HashMap<>();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			params.put(key, request.getParameter(key));			
		}
		
		params.put("requestURL", request.getRequestURL().toString());
			
		return handleCallback("kuaiqian", params, request.getCharacterEncoding());
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
}
