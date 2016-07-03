<%@ page language="java" contentType="text/html; charset=utf-8" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

</head>
<body style="margin: 0; min-height: 100%; box-sizing: border-box; background-color: #f5f5f5; ">

<form id="paymentDto" name="paymentDto" action="../api/payment/createPayment" method="POST">
    bizNo<input id="bizNo" name="bizNo" type="text" value=""/>
    accountNo<input id="accountNo" name="accountNo" type="text" value=""/>
    bizType<input id="bizType" name="bizType" type="text" value=""/>
    <input type="submit" value="Submit"/>
</form>
</body>
</html>
