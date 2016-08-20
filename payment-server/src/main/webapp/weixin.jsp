<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.json.simple.JSONObject" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>微信支付</title>
</head>
<body>
<script>
    function onBridgeReady() {
        WeixinJSBridge.invoke(
                'getBrandWCPayRequest', <%    Map paramMap = (Map)request.getAttribute("model");
                                         JSONObject json = new JSONObject(paramMap);
                                  	 out.println(json.toString());
                                  	 %>,
                function (res) {
                    //            console.log(res);
                    //            alert(res.err_msg);
                    window.location.href = "<%=request.getAttribute("returnURL").toString()%>";

                    //            if(res.err_msg == "get_brand_wcpay_request：ok" ) {
                    //              alert(res.err_msg);
                    //            } else {
                    //              alert(res.err_msg);
                    //            }
                }
        );
    }
    if (typeof WeixinJSBridge == "undefined") {
        if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
        } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
        }
    } else {
        onBridgeReady();
    }
</script>
</body>
</html>
