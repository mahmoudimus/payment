package org.klose.payment.server.integration.wechat.dto;



import org.klose.payment.server.util.MD5Util;
import org.klose.payment.server.util.ParamUtils;

import java.util.SortedMap;
import java.util.TreeMap;

public class WechatPayDto {
	
	private String appId;
	
	private String timeStamp;
	
	private String nonceStr;
	
	private String package_str;
	
	private String signType;
	
	private String paySign;
	
	private String securityKey;  
  
  public WechatPayDto(String appId, String securityKey){
	  this.appId = appId;
    this.securityKey = securityKey;
  }
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getPackage_str() {
		return package_str;
	}

	public void setPackage_str(String package_str) {
		this.package_str = package_str;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}
	
	public String getPaySign() {
		return paySign;
	}

	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}

	public String createSign(){		
		
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appId", appId);  
		packageParams.put("timeStamp", timeStamp);  
		packageParams.put("nonceStr", nonceStr);  
		packageParams.put("package", package_str);    
		packageParams.put("signType", signType);
				
		String param_str = ParamUtils.buildParams(packageParams);
		param_str = param_str + "&key=" + securityKey;
		System.out.println("WechatPayDto md5 sb:" + param_str);
		String sign = MD5Util.MD5Encode(param_str, "utf-8").toUpperCase();
		System.out.println("WechatPayDto sign:" + sign);
		
		return sign;
	}	
}
