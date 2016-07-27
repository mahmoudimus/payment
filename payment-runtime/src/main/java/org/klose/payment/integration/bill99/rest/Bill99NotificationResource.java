package org.klose.payment.integration.bill99.rest;

import org.klose.payment.integration.bill99.Bill99Helper;
import org.klose.payment.integration.bill99.Bill99PaymentService;
import org.klose.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Path("bill99")
@Component
public class Bill99NotificationResource {

	
	@Autowired
	private Bill99Helper helper;
	
	@Autowired
	private Bill99PaymentService paymentService;
	
//	private final static Logger log = LoggerFactory.getLogger(this.getClass());

	
//	@GET
//	@Path("/kuaiqian/endpoints")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response createKuaiQianPaymentParameters(
//			@Context HttpServletRequest request,
//			@QueryParam("orderId") String orderId,
//			@QueryParam("orderAmount") String orderAmount,
//			@QueryParam("policyNo") String policyNo,
//			@QueryParam("callbackURL") String callbackURL) throws UnsupportedEncodingException {
//
//		log.debug(String.format("KuaiQianPayment order request: %s callbackURL: %s", orderId, callbackURL));
//
//		IndividualAgreement policy = agreementService.retrieve(policyNo, null);
//
//		String productId = policy.getProductId();
//		String redirectURL = PaymentUtility.getKuaiqianPaymentRedirectURL(request.getRequestURL().toString(), productId);
//
//		Map<String, String> params = kqHelper.buildPaymentRequestParams(
//				orderId, orderAmount, redirectURL, callbackURL,
//				policy.getProductName(), orderAmount, policy.getProductName(),
//				policy.getProductId(), policy.getChannelCode());
//		String signature = kqHelper.sign(params);
//
//		params.put("signMsg", signature);
//		params.put("paymentEndpoint", String.format("%s/recvMerchantInfoAction.htm", kqHelper.getGatewayURL()));
//
//		return Response.ok(BeanHelper.toJSON(params), MediaType.APPLICATION_JSON).build();
//	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("/callback")
	public Response processKuaiQianNotifcation(@Context HttpServletRequest request) {
	
		@SuppressWarnings("rawtypes")
		Enumeration keys = request.getParameterNames();
		
		Map<String, Object> params = new HashMap<>();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			params.put(key, request.getParameter(key));			
		}
		
		params.put("requestURL", request.getRequestURL().toString());
			
		return paymentService.handleCallback("kuaiqian", params, request.getCharacterEncoding());
	}
	
}
