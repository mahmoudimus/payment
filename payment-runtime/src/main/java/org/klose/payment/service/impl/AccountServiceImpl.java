package org.klose.payment.service.impl;


import org.klose.payment.constant.AccountStatus;
import org.klose.payment.constant.AccountUseType;
import org.klose.payment.constant.PaymentType;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.bo.AccountInfo;
import org.klose.payment.po.AccountPO;
import org.klose.payment.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired 
  private AccountDao accountDAO;
	
	private final static Logger logger = LoggerFactory
			.getLogger(AccountServiceImpl.class);

	@Override
	public List<AccountInfo> getAccountsByPaymentType(PaymentType type) {
				
		List<Object[]> rows = type != null ? 
				accountDAO.findByPaymentType(type.getTypeId()) :
				accountDAO.getAllEffectiveAccounts();
				
		List<AccountInfo> results = new ArrayList<AccountInfo>(rows.size());
		
		for(Object[] row : rows) {
			AccountInfo acc = new AccountInfo();
			
			acc.setAccountNo((String)row[0]);
			acc.setName((String)row[1]);
			acc.setMerchantNo((String)row[2]);
			acc.setMerchantName((String)row[3]);
			acc.setStatus(AccountStatus.valueOf((int)row[4]));
			acc.setType(PaymentType.valueOf((int)row[5]));
			acc.setUseType(AccountUseType.valueOf((int)row[6]));
			
			results.add(acc);
		}
		
		return results;
	}

	@Override
	public AccountInfo getAccountbyNo(String accountNo) {
		
		AccountInfo acc = null;
		
		if(accountNo != null) {		
			AccountPO po = accountDAO.findByAccountNo(accountNo);
			PaymentType type = PaymentType.valueOf(po.getType());

			if(po != null){
				acc = new AccountInfo();
				acc.setAccountNo(po.getAccountNo());
				acc.setName(po.getName());
				acc.setMerchantNo(po.getMerchantNo());
				acc.setMerchantName(po.getMerchantName());
				acc.setStatus(AccountStatus.valueOf(po.getStatusId()));
				acc.setType(type);
				acc.setUseType(AccountUseType.valueOf(po.getUseType()));
				acc.setWXOpenIdRequired(PaymentType.WX_JSAPI.equals(type));
			}			
		}
		
		return acc;		
	}

	@Override
	public String getProcessBeanByNo(String accountNo) {
		String result = null;
		
		if(accountNo != null) {		
			AccountPO po = accountDAO.findByAccountNo(accountNo);
			
			if(po != null){
				result = po.getProcessBean();
			}			
		}
		
		return result;
	}

	@Override
	public boolean isAccountUnderTesting(String accountNo) {
		
		AccountInfo acc = getAccountbyNo(accountNo);
		
		return acc != null && acc.getStatus().equals(AccountStatus.Testing);
		
	}
}
