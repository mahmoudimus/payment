package org.klose.payment.service;


import org.klose.payment.bo.AccountInfo;

import java.util.Map;

public interface AccountService {

	AccountInfo getAccountbyNo(String accountNo);
	
	String getProcessBeanByNo(String accountNo);
	
	boolean isAccountUnderTesting(String accountNo);

	Map<String, Object> parseConfigData(String accountNo);
	
}
