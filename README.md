# payment-gateway
## 目标
和支付宝，微信JS，快钱等第三方支付进行集成，单独部署的支付网关，用于浏览器内的支付集成

## 设计

### 配置
* t_payment_ext_conf表用于保存客户化的prepay callback的spring bean名称
* t_payment_account表用于保存支付账户的配置信息
* t_payment_transaction表用于保存每笔订单的实例信息

### 流程
1. 在数据库配置支付帐号相关信息
2. 根据商品订单信息，签名后，产生html表单
3. 通过http或者https发送给各大支付平台
4. 页面跳转到各大支付平台进行支付
5. 支付完成后进行前台和后台回调
6. 前台回调主要用于展示支付结果
7. 后台回调主要用于交易完成

### 代码组织：
* payment-common:　一系列基础库，例如签名，日期，日志等

* payment-api: 定义一系列接口, 会在payment-server代码中被调用　
** `org.klose.payment.api.PaymentProxy`:　创建，更新支付信息，查询支付账户配置等
** `org.klose.payment.api.CallBackAgent`: 后台回调中的业务逻辑

* payment-runtime: ｀PaymentProxy｀接口的实现　
** `org.klose.payment.integration`:　主要实现各大支付平台发送数据和后台调用逻辑

* payment-server: 页面jsp代码和restful服务
** `testPay.jsp`：是主入口，调用｀org.klose.payment.server.rest.PaymentResource#createPayment｀这个restful服务
** `paymentForm.jsp`：发送支付表单给第三方支付
** `paymentResult.jsp`：一个简单展示前台回调的默认实现


### 客户化：

实现'org.klose.payment.server.prepare.PaymentIntegrationService'：准备商品，价格，订单等各种信息
实现'org.klose.payment.api.CallBackAgent'：后台回调中“完成业务事物交易”


## License
Copyright ©  2016 
Distributed under the Appache Public License version 2.0
