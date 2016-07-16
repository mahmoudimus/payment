package org.klose.payment.integration.wechat.dto;

import org.klose.payment.util.MD5Util;
import org.klose.payment.util.ParamUtils;


import java.util.SortedMap;
import java.util.TreeMap;


public class WechatPrepayRequestDto {	

	private String appid;	

	private String mch_id;	

	private String nonce_str;
	
	private String body;	

	private String out_trade_no;	

	private Integer total_fee;	

	private String spbill_create_ip;
	
	private String notify_url;	

	private String trade_type;
	
	private String productId; 

	private String openid;
	
	private String securityKey;	
	
	public WechatPrepayRequestDto(String appId,String mchId,String securityKey){
		this.appid = appId;
		this.mch_id = mchId;
		this.securityKey = securityKey;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public Integer getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(Integer total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public String createSign(){
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", this.appid);  
		packageParams.put("mch_id", this.mch_id);  
		packageParams.put("nonce_str", nonce_str);  
		packageParams.put("body", body);  
		packageParams.put("out_trade_no", out_trade_no);
		packageParams.put("total_fee", String.valueOf(total_fee));
		packageParams.put("spbill_create_ip", spbill_create_ip);  
		packageParams.put("notify_url", notify_url);  
		packageParams.put("trade_type", trade_type);  		
		packageParams.put("openid", openid);
		
		boolean nativeFlag = "NATIVE".equals(this.getTrade_type());
		if(nativeFlag)
		  packageParams.put("product_id", productId);

		
		String param_str = ParamUtils.buildParams(packageParams);
		param_str = param_str + "&key=" + securityKey;
		
		System.out.println("WechatPrepayRequestDto md5 sb:" + param_str);
		String sign = MD5Util.MD5Encode(param_str, "utf-8").toUpperCase();
		System.out.println("WechatPrepayRequestDto sign:" + sign);
		return sign;
	}
	
	public String getWechatXml(){
		boolean nativeFlag = "NATIVE".equals(this.getTrade_type());
		String xmlStr = "";
		Integer totalFee = this.getTotal_fee();
		
		
		xmlStr = xmlStr 
				+ "<xml>"
				+ "<appid>" + this.getAppid() + "</appid>"
				+ "<mch_id>" + this.getMch_id() + "</mch_id>"
				+ "<nonce_str>" + this.getNonce_str() + "</nonce_str>"				
				+ "<body>" + this.getBody() + "</body>"
				+ "<out_trade_no>" + this.getOut_trade_no() + "</out_trade_no>"
				//+ "<total_fee>" + String.valueOf(this.getTotal_fee()) + "</total_fee>"
				+ "<total_fee>"+ totalFee +"</total_fee>"
				+ "<spbill_create_ip>" + this.getSpbill_create_ip() + "</spbill_create_ip>"
				+ "<notify_url>" + this.getNotify_url() + "</notify_url>"
				+ "<trade_type>" + this.getTrade_type() + "</trade_type>"
				+ (nativeFlag ? 
						("<product_id>"+this.getProductId()+"</product_id>") : 
							("<openid>" + this.getOpenid() + "</openid>") )
				+ "<sign>" + this.createSign() + "</sign>"
				+ "</xml>";
		return xmlStr;
	}

}
