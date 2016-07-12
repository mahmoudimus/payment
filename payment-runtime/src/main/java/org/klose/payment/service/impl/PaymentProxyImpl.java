package org.klose.payment.service.impl;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.FrontPageForwardType;
import org.klose.payment.api.PaymentProxy;
import org.klose.payment.bo.AccountInfo;
import org.klose.payment.bo.BillingData;
import org.klose.payment.bo.PaymentForm;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.common.context.ApplicationContextUtils;
import org.klose.payment.constant.PaymentConstant;
import org.klose.payment.service.AccountService;
import org.klose.payment.service.PaymentService;
import org.klose.payment.service.TransactionDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentProxyImpl implements PaymentProxy {
	@Autowired
	AccountService accountService;
	
	@Autowired
	TransactionDataService transactionService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final static String ERR_MSG_PAYMENT_PAID = "该笔订单已支付完成！ 请勿重复支付！";
	
	@Override
	public PaymentForm createPayment(BillingData bill) throws Exception {
		
		String bizNo = bill.getBizNo();
		PaymentResult successfulPayment =
				transactionService.findSuccessfulPaymentByBizNo(bizNo);
		
		if(successfulPayment != null) {
			String forwardURL = successfulPayment.getExtData().get(
					PaymentConstant.KEY_PAYMENT_RETURN_URL);
			
			if(forwardURL == null || forwardURL.isEmpty()) {
				throw new RuntimeException(ERR_MSG_PAYMENT_PAID);
			}
			
			PaymentForm result = new PaymentForm();
			result.setForwardType(FrontPageForwardType.REDIRECT);
			result.setForwardURL(forwardURL);
			
			return result;
		}
		
		String accountNo = bill.getAccountNo();
		
		if(accountService.isAccountUnderTesting(accountNo)) {
			bill.setPrice(PaymentConstant.TESTING_PAY_AMOUNT);
			logger.info("Testing payment account using 0.01 as payment's price !!");
		}

		if(accountNo != null){
			String procerssBeanName = accountService.getProcessBeanByNo(accountNo);
			
			if(procerssBeanName != null){
				PaymentService paymentService =
						ApplicationContextUtils.getBean(procerssBeanName,	PaymentService.class);
				
				if(paymentService != null){
					PaymentForm result = paymentService.generatePaymentData(bill);
					result.setReturnURL(
							transactionService.processReturnURL(
									result.getTransactionId(), bill.getReturnURL()));						
					
					return result;
				}
			}
		}	
			
		return null;
	}

	@Override
	public PaymentResult queryPayment(Long transactionId) {

		return transactionService.findPaymentByTransactionId(transactionId);		
	}

	@Override
	public AccountInfo getAccountbyNo(String accountNo) {

		return accountService.getAccountbyNo(accountNo);
	}

	@Override
	public String findReturnUrl(Long transactionId) {
		Assert.isNotNull(transactionId);
		return transactionService.findReturnURL(transactionId);
	}

	@Override
	public Map<String, Object> parseConfig(String accountNo) {
		return accountService.parseConfigData(accountNo);
	}

	@Override
	public void updatePaymentResult(
			Long transId, boolean isSuccess, String payId, String notifyMsg) {

		transactionService.updatePaymentResult(transId, isSuccess, payId, notifyMsg);

		return;
	}

}
