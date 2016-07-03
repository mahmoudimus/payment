package org.klose.payment.server.service.impl;


import org.klose.payment.server.api.PaymentProxy;
import org.klose.payment.server.bo.AccountInfo;
import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.ForwardViewData;
import org.klose.payment.server.bo.PaymentResult;
import org.klose.payment.server.common.context.ApplicationContextUtils;
import org.klose.payment.server.constant.FrontPageForwardType;
import org.klose.payment.server.constant.PaymentConstant;
import org.klose.payment.server.constant.PaymentType;
import org.klose.payment.server.service.AccountService;
import org.klose.payment.server.service.EbaoPaymentService;
import org.klose.payment.server.service.TransactionDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentProxyImpl implements PaymentProxy {
	@Autowired
	AccountService accountService;
	
	@Autowired
	TransactionDataService transactionService;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final static String ERR_MSG_PAYMENT_PAID = "该笔订单已支付完成！ 请勿重复支付！";
	
	@Override
	public ForwardViewData createPayment(BillingData bill) throws Exception {
		
		String bizNo = bill.getBizNo();
		PaymentResult successfulPayment =
				transactionService.findSuccessfulPaymentByBizNo(bizNo);
		
		if(successfulPayment != null) {
			String forwardURL = successfulPayment.getExtData().get(
					PaymentConstant.KEY_PAYMENT_RETURN_URL);
			
			if(forwardURL == null || forwardURL.isEmpty()) {
				throw new RuntimeException(ERR_MSG_PAYMENT_PAID);
			}
			
			ForwardViewData result = new ForwardViewData();
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
				EbaoPaymentService paymentService =
						ApplicationContextUtils.getBean(procerssBeanName,	EbaoPaymentService.class);
				
				if(paymentService != null){
					ForwardViewData result = paymentService.generatePaymentData(bill);
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
	public List<AccountInfo> getAccountsByPaymentType(PaymentType type) {
		
		return accountService.getAccountsByPaymentType(type);
	}

	@Override
	public AccountInfo getAccountbyNo(String accountNo) {

		return accountService.getAccountbyNo(accountNo);
	}

	@Override
	public void updatePaymentResult(
			Long transId, boolean isSuccess, String payId, String notifyMsg) {

		transactionService.updatePaymentResult(transId, isSuccess, payId, notifyMsg);
		
		return;
	}

}
