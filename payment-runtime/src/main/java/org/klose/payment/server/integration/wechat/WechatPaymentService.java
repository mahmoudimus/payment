package org.klose.payment.server.integration.wechat;


import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jdom.JDOMException;
import org.klose.payment.server.common.utils.XMLUtils;
import org.klose.payment.server.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class WechatPaymentService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final static String WECHAT_RESPONSE_MSG = 
			"<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";


	public Response handleCallback(String proxyId, Map params, String encoding) {	
		
		Map<String, String> notifyParams = null;
		
		try {
			notifyParams = XMLUtils.parseToMap(params.get("xml").toString(), "UTF-8");
		} catch (JDOMException | IOException e1) {
			e1.printStackTrace();
		}
		
		String paymentResult = notifyParams.get("return_code");
		String quoteNumber = notifyParams.get("out_trade_no");		
		
		if(verifySign(notifyParams, "WXSecurityKey", encoding != null ? encoding : "UTF-8") 
			 && quoteNumber!=null) {		
			
			String payId = notifyParams.get("transaction_id");					
			
			try{
				if("SUCCESS".equals(paymentResult)){				
					
				}
			}
			
			catch (Exception e) {

			}
		}
		
		String return_msg = String.format(WECHAT_RESPONSE_MSG, paymentResult);
		return Response.status(Response.Status.OK).entity(return_msg).type(MediaType.APPLICATION_XML_TYPE).build();
	}

	public Map parseRequestData(String proxyId, HttpServletRequest request) {
		try {
			Map<String,String> resultMap = new HashMap<String,String>();
			InputStream is = request.getInputStream();
			resultMap.put("xml", IOUtils.toString(is));
			return resultMap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new HashMap();
		}
	}
			
	private boolean verifySign(Map params, String securityKey, String encoding){
		SortedMap<String, String> sortedParams = new TreeMap<String, String>();
		
		Iterator iter = params.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			sortedParams.put(key, params.get(key) != null ? params.get(key).toString().trim() : "");
		}
		
		StringBuffer sb = new StringBuffer();		
		
		Set es = sortedParams.entrySet();
		iter = es.iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry)iter.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		
		sb.append("key=".concat(securityKey));
		
		String sign = MD5Util.MD5Encode(sb.toString(), encoding).toLowerCase();
		String tenpaySign = params.get("sign").toString().toLowerCase();		
		
		return tenpaySign.equals(sign);
	}
	
	public static void main(String[] args) {
    String content = "<xml><appid><![CDATA[wx9ab9b4105f111a85]]></appid>" +
    		"<bank_type><![CDATA[SPDB_DEBIT]]></bank_type>" +
    		"<cash_fee><![CDATA[1]]></cash_fee>" +
    		"<fee_type><![CDATA[CNY]]></fee_type>" +
    		"<is_subscribe><![CDATA[N]]></is_subscribe>" +
    		"<mch_id><![CDATA[1289029101]]></mch_id>" +
    		"<nonce_str><![CDATA[160324162031GD88JHTWN4]]></nonce_str>" +
    		"<openid><![CDATA[oh_rtv3FmUFAGoW5xUQzdv6Kdfto]]></openid>" +
    		"<out_trade_no><![CDATA[EbaoTechMsIns1458807624593]]></out_trade_no>" +
    		"<result_code><![CDATA[SUCCESS]]></result_code>" +
    		"<return_code><![CDATA[SUCCESS]]></return_code>" +
    		"<sign><![CDATA[E6D242D00CB21E1E128B2DE1453662D4]]></sign>" +
    		"<time_end><![CDATA[20160324162039]]></time_end>" +
    		"<total_fee>1</total_fee>" +
    		"<trade_type><![CDATA[JSAPI]]></trade_type>" +
    		"<transaction_id><![CDATA[4006262001201603244239726827]]></transaction_id>" +
    		"</xml>";
		Document document;
		try {
			document = DocumentHelper.parseText(content);
			
			Element rootElement = document.getRootElement();      
      
      Map gh;
			try {
				gh = XMLUtils.parseToMap(content, null);
				System.out.println(gh.get("return_code")); 
				
				//verifySign(gh, "UTF-8");
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  
	}
}
