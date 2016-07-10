package org.klose.payment.integration.alipay;


import org.klose.payment.common.utils.Assert;
import org.klose.payment.constant.PaymentType;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.integration.alipay.config.AlipayConfig;
import org.klose.payment.common.utils.JSONHelper;
import org.klose.payment.po.AccountPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AlipayConfigService {
    @Autowired
    private AccountDao dao;

    private final static Logger logger = LoggerFactory
            .getLogger(AlipayConfigService.class);

    @SuppressWarnings("unchecked")
    public AlipayConfig initializeConfig(String accountNo) {
        Assert.isNotNull(accountNo, "account no is null");
        logger.debug("initialize config [accountNo = {}]", accountNo);

        AccountPO po = dao.findByAccountNo(accountNo);
        Assert.isNotNull(po, "account po is null");
        logger.trace("quried account po : {}", po);

        String gatewayURL = po.getGatewayURL();
        Assert.isNotNull(gatewayURL, "gate way is null");
        String configData = po.getConfigData();
        Assert.isNotNull(configData, "config data is null");
        logger.trace("config data string = {}", configData);

        boolean isWAPPayment = PaymentType.ALIPAY_WAP_GATEWAY
                .equals(PaymentType.valueOf(po.getType()));

        AlipayConfig config = new AlipayConfig();
        Map<String, Object> jsonMap = (Map<String, Object>) JSONHelper
                .parse(configData);
        config.setWapPayment(PaymentType.ALIPAY_WAP_GATEWAY.equals(PaymentType
                .valueOf(po.getType())));

        config.setPartner((String) jsonMap.get("partner"));
        config.setKey((String) jsonMap.get("securityKey"));
        config.setWapPayment(isWAPPayment);
        config.setSellerEmail((String) jsonMap.get("sellerMail"));
        config.setShowUrl(isWAPPayment ? (String) jsonMap.get("show_url") : "");
        config.setGateway(gatewayURL);

        logger.debug("initialized config [config = {}]", config);
        return config;
    }

}
