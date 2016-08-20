package org.klose.payment.service.impl;


import org.klose.payment.bo.PaymentExtensionConf;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.po.PaymentExtensionConfPO;
import org.klose.payment.dao.PaymentExtensionConfDao;
import org.klose.payment.service.PaymentExtensionConfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentExtensionConfServiceImpl implements
		PaymentExtensionConfService {

	@Autowired
	private PaymentExtensionConfDao confDao;

	private Logger logger = LoggerFactory
			.getLogger(PaymentExtensionConfServiceImpl.class);

	@Override
	public PaymentExtensionConf getPaymentExtensionByBizTypeAndAccountType(
			Integer accountType, String bizType) {
		Assert.isNotNull(accountType, "account type is empty");
		logger.info("start get payment extension conf");
		logger.debug("[accountType = {}, bizType = {}]", accountType, bizType);
		PaymentExtensionConf conf = null;
		PaymentExtensionConfPO po = confDao.findByBizTypeAndAccountType(
				accountType, bizType);
		if (po != null) {
			conf = new PaymentExtensionConf();
			BeanUtils.copyProperties(po, conf);
		}

		logger.debug("finished get payment extension conf");
		logger.debug("[queried payment conf : {}]", conf);

		return conf;
	}

}
