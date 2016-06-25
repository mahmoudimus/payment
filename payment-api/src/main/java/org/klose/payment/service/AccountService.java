package org.klose.payment.service;



import org.klose.payment.bo.AccountInfo;
import org.klose.payment.constant.PaymentType;

import java.util.List;

public interface AccountService {
	
	public List<AccountInfo> getAccountsByPaymentType(PaymentType type);
	
	public AccountInfo getAccountbyNo(String accountNo);
	
	public String getProcessBeanByNo(String accountNo);
	
	public boolean isAccountUnderTesting(String accountNo);
	
}
