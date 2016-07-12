package org.klose.payment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayFormSubmit {
	
	private final static Logger logger = LoggerFactory.getLogger(PayFormSubmit.class);

	public static String buildRequest(String url, Map<String, Object> sParaTemp, String strMethod){
		return buildRequest(url,sParaTemp,strMethod,"OK");
	}

  public static String buildRequest(String url, Map<String, Object> sParaTemp, String strMethod, String strButtonName) {
  	
    List<String> keys = new ArrayList<String>(sParaTemp.keySet());

    StringBuffer sbHtml = new StringBuffer();

    sbHtml.append("<form id=\"paySubmitForm\" name=\"paySubmitForm\" action=\"" + url
                  //+ "_input_charset=" + AlipayConfig.input_charset 
                  + "\" method=\"" + strMethod
                  + "\">");

    boolean includeSizeFlag = false;
    Map<String, String> formParaSizes = null;
    if(sParaTemp.containsKey("formParaSize") ){
    	formParaSizes = (Map<String, String>) sParaTemp.get("formParaSize");
    	includeSizeFlag = true;
    }
    
    for (int i = 0; i < keys.size(); i++) {
        String name = (String) keys.get(i);
        if( !"formParaSize".equals(name)){
        	String value = (String) sParaTemp.get(name);
        	if(includeSizeFlag){
        		sbHtml.append("<input type=\"hidden\" size=\""+ formParaSizes.get(name+"Size") +"\" name=\"" + name + "\" value=\"" + value + "\"/>");
        	}else{
        		sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        	}
        }
    }

    //sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
    sbHtml.append("<script>document.forms['paySubmitForm'].submit();</script></form>");

    //logger.debug(sbHtml.toString());
    
    return sbHtml.toString();
  }
}
