<%@ page language="java" contentType="text/html; charset=utf-8"%>

<%@ page import="java.util.Map" %>
<%@ page import="org.klose.payment.server.constant.PaymentConstant" %>
<%@ page import="org.klose.payment.server.util.PayFormSubmit" %>
<!doctype html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="apple-mobile-web-app-capable" content="yes">
  <meta name="apple-mobile-web-app-status-bar-style" content="black">
  <meta name="format-detection" content="telephone=no">
  <meta content="email=no" name="format-detection">
  <meta name="msapplication-tap-highlight" content="no" />
</head>
<body style = "margin: 0; min-height: 100%; box-sizing: border-box; background-color: #f5f5f5; ">
  <div style = "margin: 0; height: 40px; font-size: 16px; font-weight: bold; color: #fff; text-align: center; line-height: 40px; background-color: #02bb77;
    border-bottom: 1px solid #ff9913;">
		<p style = "margin: 0;">进行支付</p>
	</div>
	<br/>
  <div style = "padding: 2px; margin: 0; background-color: #ffffff;  line-height: 25px;">
    <p style="text-align: center; font-size: 18px;">正在跳转到支付页面,<br/>请稍候 ~</p>
  </div>

	<% 
		String endPoint = (String)request.getAttribute(PaymentConstant.KEY_PAYMENT_ENDPOINT);
		Map paramMap = (Map)request.getAttribute("model");
		
		String sHtmlText = PayFormSubmit.buildRequest(endPoint, paramMap, "post");
		out.println(sHtmlText);
	%>

</body>
</html>