<%@ page language="java" contentType="text/html; charset=utf-8" %>
<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript"> 
	function isWeiXin(){
	    var ua = window.navigator.userAgent.toLowerCase();
	    if(ua.match(/MicroMessenger/i) == 'micromessenger'){
	        return true;
	    }else{
	        return false;
	    }
	}

	function submitForm() {
		var orderForm = document.getElementById('orderDto'); 
		if(isWeiXin()) 
			orderForm.action = "<%=request.getContextPath() %>/api/wechat/oauth";
		else 
			orderForm.action = "<%=request.getContextPath() %>/api/payment/create"; 
		orderForm.submit(); 
	}

	window.onload = function() {
        document.getElementById('submitBtn').onclick = submitForm;
    };
</script>
</head>
<body style="margin: 0; min-height: 100%; box-sizing: border-box; background-color: #f5f5f5; ">

<form id="orderDto" name="orderDto" method="POST">
    bizNo<input id="bizNo" name="bizNo" type="text" value=""/>
    accountNo<input id="accountNo" name="accountNo" type="text" value=""/>
	<button id="submitBtn"> submit</button>
</form>
</body>
</html>
