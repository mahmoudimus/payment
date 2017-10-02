package org.klose.payment.integration.wechat;


import org.apache.commons.io.IOUtils;
import org.jdom.JDOMException;
import org.klose.payment.common.exception.GeneralRuntimeException;
import org.klose.payment.common.utils.Assert;
import org.klose.payment.common.utils.JSONHelper;
import org.klose.payment.common.utils.LogUtils;
import org.klose.payment.common.utils.XMLUtils;
import org.klose.payment.common.utils.sign.MD5Util;
import org.klose.payment.dao.AccountDao;
import org.klose.payment.dao.TransactionDao;
import org.klose.payment.integration.wechat.constant.WeChatConstant;
import org.klose.payment.po.AccountPO;
import org.klose.payment.po.TransactionPO;
import org.klose.payment.service.notification.ProcessNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RequestMapping(value = "payment/wechat")
@RestController
public class WechatNotificationResources {

    @Resource
    private AccountDao accountDao;

    @Resource
    private TransactionDao transactionDao;

    @Resource
    private ProcessNotificationService notificationService;


    private final static String WECHAT_RESPONSE_MSG =
            "<xml><return_code><![CDATA[%s]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")

    @RequestMapping(value = "/notification", method = RequestMethod.POST, produces = APPLICATION_XML_VALUE)
    public String handlePaymentCallback(HttpServletRequest request) {

        String encoding = request.getCharacterEncoding();
        Map<String, String> params = parseRequestData(request);
        String notifyData = params.get("xml");

        Map<String, String> notifyParams;
        try {
            notifyParams = XMLUtils.parseToMap(notifyData, encoding);
            logger.debug("notifyParams : {}", LogUtils.getMapContent(notifyParams));

        } catch (JDOMException | IOException e1) {
            throw new GeneralRuntimeException("exception occurs by parsing response");
        }

        Assert.isNotNull(notifyParams);
        String paymentResult = notifyParams.get("return_code");
        String orderNo = notifyParams.get("out_trade_no");

        TransactionPO transaction = transactionDao.findByTransactionNo(orderNo);
        AccountPO account = accountDao.findByAccountNo(transaction.getAccountNo());
        Map<String, Object> accountConfig =
                (Map<String, Object>) JSONHelper.parse(account.getConfigData());

        if (verifySign(notifyParams,
                (String) accountConfig.get(WeChatConstant.KEY_WEIXIN_SECURITY),
                encoding != null ? encoding : "UTF-8") &&
                orderNo != null) {
            notificationService.handlePaymentCallback(
                    orderNo, notifyData, notifyParams.get("transaction_id"), "", null);
        }

        return String.format(WECHAT_RESPONSE_MSG, paymentResult);
    }

    private Map parseRequestData(HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();

        try {
            InputStream is = request.getInputStream();
            resultMap.put("xml", IOUtils.toString(is));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        for (String key : resultMap.keySet())
            logger.debug("key = {}, value = {}", key, resultMap.get(key));

        return resultMap;
    }

    @SuppressWarnings("rawtypes")
    private boolean verifySign(Map params, String securityKey, String encoding) {
        SortedMap<String, String> sortedParams = new TreeMap<>();

        Iterator iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            sortedParams.put(key, params.get(key) != null ? params.get(key).toString().trim() : "");
        }

        StringBuilder sb = new StringBuilder();

        Set es = sortedParams.entrySet();
        iter = es.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }

        sb.append("key=").append(securityKey);

        String sign = MD5Util.MD5Encode(sb.toString(), encoding).toLowerCase();
        String tenpaySign = params.get("sign").toString().toLowerCase();

        return tenpaySign.equals(sign);
    }
}
