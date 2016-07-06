package org.klose.payment.server.service;



import org.klose.payment.server.bo.AccountInfo;
import org.klose.payment.server.constant.PaymentType;

import java.util.List;

public interface AccountService {
	
	List<AccountInfo> getAccountsByPaymentType(PaymentType type);
	
	AccountInfo getAccountbyNo(String accountNo);
	
	String getProcessBeanByNo(String accountNo);
	
	boolean isAccountUnderTesting(String accountNo);
	
}
