package org.klose.payment.integration.bill99;

import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.LogUtils;
import org.klose.payment.common.utils.ParamUtils;
import org.klose.payment.common.utils.sign.SHA1withRSAUtils;
import org.klose.payment.integration.bill99.constant.Bill99Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Bill99Helper {


    private final static Logger logger = LoggerFactory.getLogger(Bill99Helper.class);

    public static boolean verify(Map<String, String> params, String publicKeyPath) {
        Assert.isNotNull(params);
        Assert.isNotNull(publicKeyPath);
        logger.info("start verify 99bill payment response");
        logger.debug("[publicKeyPath = {} \n, params : {} ]",
                publicKeyPath, LogUtils.getMapContent(params));

        String signStr = ParamUtils.buildParams(params, Bill99Constant.RETURN_PARAMETERS);
        String receivedSign = params.get("signMsg");
        logger.trace("signStr = {}, received sign = {}",
                signStr, receivedSign);
        boolean result = SHA1withRSAUtils.enCodeByCer(publicKeyPath, "UTF-8", signStr, receivedSign);
        logger.debug("[result = {}]", result);
        return result;

    }

    public static String sign(Map<String, String> map,
                              String privateKeyPath, String privateKeyPassword) {
        Assert.isNotNull(map);
        Assert.isNotNull(privateKeyPath);
        Assert.isNotNull(privateKeyPassword);

        logger.info("start create signature");
        for (String key : map.keySet())
            logger.trace("parameter key = {}, value = {}", key, map.get(key));

        String paramStr = ParamUtils.buildParams(map, Bill99Constant.REQUEST_PARAMETERS);
        Assert.isNotNull(paramStr);

        logger.trace("paramStr = {}, privateKeyPath = {}, privateKeyPassword = {}",
                paramStr, privateKeyPath, privateKeyPassword);

        String signature = SHA1withRSAUtils.sign(privateKeyPath,
                privateKeyPassword, "UTF-8", paramStr);
        logger.info("finish create payment signature");
        logger.debug("[payment signature = {} ]", signature);

        return signature;
    }
}
