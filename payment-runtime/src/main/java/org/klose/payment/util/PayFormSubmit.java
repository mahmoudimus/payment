package org.klose.payment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayFormSubmit {
    private final static Logger logger = LoggerFactory.getLogger(PayFormSubmit.class);

    @SuppressWarnings("unchecked")
    public static String buildRequest(String url, Map<String, Object> sParaTemp, String strMethod) {

        List<String> keys = new ArrayList<>(sParaTemp.keySet());

        StringBuilder sbHtml = new StringBuilder();

        sbHtml.append("<form id=\"paySubmitForm\" name=\"paySubmitForm\" action=\"").
                append(url).append("\" method=\"").append(strMethod).append("\">");

        boolean includeSizeFlag = sParaTemp.containsKey("formParaSize");
        Map<String, String> formParaSizes =
                (Map<String, String>) sParaTemp.get("formParaSize");

        for (String name : keys) {
            if (!"formParaSize".equals(name)) {
                String value = (String) sParaTemp.get(name);
                if (includeSizeFlag) {
                    sbHtml.append("<input type=\"hidden\" size=\"").append(formParaSizes.get(name + "Size")).append("\" name=\"").append(name).append("\" value=\"").append(value).append("\"/>");
                } else {
                    sbHtml.append("<input type=\"hidden\" name=\"").append(name).append("\" value=\"").append(value).append("\"/>");
                }
            }
        }

        //sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['paySubmitForm'].submit();</script></form>");

        logger.debug("html code : \n {}", sbHtml.toString());

        return sbHtml.toString();
    }
}
