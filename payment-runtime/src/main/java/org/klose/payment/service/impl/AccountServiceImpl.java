package org.klose.payment.service.impl;


import org.klose.payment.bo.AccountInfo;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.JSONHelper;
import org.klose.payment.constant.AccountStatus;
import org.klose.payment.constant.AccountUseType;
import org.klose.payment.constant.PaymentType;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.po.AccountPO;
import org.klose.payment.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {
	
	@Autowired 
  private AccountDao accountDAO;
	
	private final static Logger logger = LoggerFactory
			.getLogger(AccountServiceImpl.class);

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

	@Override
	public Map<String, Object> parseConfigData(String accountNo) {
		Assert.isNotNull(accountNo, "account no is null");
		logger.debug("initialize config [accountNo = {}]", accountNo);

		AccountPO po = accountDAO.findByAccountNo(accountNo);
		Assert.isNotNull(po, "account po is null");
		logger.trace("queried account po : {}", po);

		String configData = po.getConfigData();
		Assert.isNotNull(configData, "config data is null");
		logger.trace("config data string = {}", configData);

		Map<String, Object> config = (Map<String, Object>) JSONHelper
				.parse(configData);
		config.put("gateway", po.getGatewayURL());
		config.put("type", po.getType());

		logger.debug("initialized config [config = {}]", config);
		return config;
	}
}
