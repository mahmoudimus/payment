package org.klose.payment.integration.wechat;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.common.utils.JSONHelper;
import org.klose.payment.integration.wechat.config.WeChatConfig;
import org.klose.payment.po.AccountPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WeChatConfigService {

	@Autowired
	private AccountDao dao;

	private final static Logger logger = LoggerFactory
			.getLogger(WeChatConfigService.class);

	@SuppressWarnings("unchecked")
	public WeChatConfig initializeConfig(String accountNo) {
		Assert.isNotNull(accountNo, "account no is null");
		logger.info("initialize wechat config ");
		logger.debug("[accountNo = {}]", accountNo);

		AccountPO po = dao.findByAccountNo(accountNo);
		Assert.isNotNull(po, "account po is null");
		logger.trace("quried account po : {}", po);

		String gatewayURL = po.getGatewayURL();
		Assert.isNotNull(gatewayURL, "gate way is null");
		String configData = po.getConfigData();
		Assert.isNotNull(configData, "config data is null");
		logger.trace("config data string = {}", configData);

		WeChatConfig config = new WeChatConfig();
		Map<String, Object> jsonMap = (Map<String, Object>) JSONHelper
				.parse(configData);
		config.setAppId((String) jsonMap.get("appID"));
		config.setAppSecret((String) jsonMap.get("appSecret"));
		config.setMchId((String) jsonMap.get("mchID"));
		config.setSecurityKey((String) jsonMap.get("securityKey"));

		logger.debug("initialized config [config : {} ]", config);
		return config;
	}
}
