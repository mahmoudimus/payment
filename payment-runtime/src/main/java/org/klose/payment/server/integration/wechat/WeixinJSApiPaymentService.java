package org.klose.payment.server.integration.wechat;


import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.ForwardViewData;
import org.klose.payment.server.common.utils.Assert;
import org.klose.payment.server.common.utils.JSONHelper;
import org.klose.payment.server.constant.FrontPageForwardType;
import org.klose.payment.server.constant.PaymentConstant;
import org.klose.payment.server.dao.AccountDao;
import org.klose.payment.server.integration.wechat.dto.WechatPayDto;
import org.klose.payment.server.integration.wechat.dto.WechatPrepayResponseDto;
import org.klose.payment.server.po.AccountPO;
import org.klose.payment.server.service.EbaoPaymentService;
import org.klose.payment.server.util.ValidateCode;
import org.klose.payment.server.service.AccountService;
import org.klose.payment.server.service.TransactionDataService;
import org.klose.payment.server.util.ParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component(value = "WeixinJSApiPaymentService")
public class WeixinJSApiPaymentService implements EbaoPaymentService {

	@Autowired
	AccountService accountService;

	@Autowired
	AccountDao accountDao;

	@Autowired
	TransactionDataService transactionService;

	@Override
	public ForwardViewData generatePaymentData(BillingData bill)
			throws Exception {
		WechatPrepayData prepayData = convertBillingData(bill);

		WechatPrepayResponseDto prepayResponse = WechatPrepayService.getPrepay(
				prepayData, bill.getHostEndpoint());

		Map<String, Object> extData = bill.getExtData() != null ? bill
				.getExtData() : new HashMap<String, Object>(1);

		extData.put(PaymentConstant.KEY_WEIXIN_PREPAY_ID,
				prepayResponse.getPrepay_id());

		ForwardViewData result = generateForwardViewData(prepayResponse);

		result.setTransactionId(transactionService
				.createTransactionFromBillingData(bill));

		return result;
	}

	@SuppressWarnings("unchecked")
	private WechatPrepayData convertBillingData(BillingData bill) {
		AccountPO account = accountDao.findByAccountNo(bill.getAccountNo());
		Map<String, Object> accountConfig = (Map<String, Object>) JSONHelper
				.parse(account.getConfigData());

		Map<String, Object> extData = bill.getExtData();

		WechatPrepayData result = new WechatPrepayData();
		String mchId = (String) accountConfig
				.get(PaymentConstant.KEY_WEIXIN_MCH_ID);
		Assert.isNotNull(mchId);
		// result.setOrderNo(bill.getBizNo());
		result.setOrderNo(ParamUtils.getOrderNo(mchId));
		bill.setOrderNo(result.getOrderNo());
		result.setProductName(bill.getSubject());
		result.setTotal_fee(bill.getPrice().multiply(
				new BigDecimal(bill.getQuantity())));
		result.setTradeType(PaymentConstant.WEIXIN_TRADETYPE_JSAPI); 
		result.setProductId((String) (extData
				.get(PaymentConstant.KEY_WEIXIN_PRODUCT_ID)));

		result.setAppId((String) accountConfig
				.get(PaymentConstant.KEY_WEIXIN_APP_ID));
		result.setMchId(mchId);
		result.setMchSecret((String) accountConfig
				.get(PaymentConstant.KEY_WEIXIN_SECURITY));
		result.setOpenId((String) (extData
				.get(PaymentConstant.KEY_WEIXIN_OPENID)));

		return result;
	}

	private ForwardViewData generateForwardViewData(
			WechatPrepayResponseDto prepayResponse) {

		String nonceStr = ValidateCode.randomCode(10);
		String timeStamp = String.valueOf(new Date().getTime() / 1000);

		WechatPayDto payDto = new WechatPayDto(prepayResponse.getAppId(),
				prepayResponse.getSecurityKey());
		payDto.setNonceStr(nonceStr);
		payDto.setTimeStamp(timeStamp);
		payDto.setPackage_str("prepay_id=" + prepayResponse.getPrepay_id());
		payDto.setSignType("MD5");
		payDto.setPaySign(payDto.createSign());

		Map<String, String> PayDtoResult = new HashMap<String, String>();
		PayDtoResult.put("appId", payDto.getAppId());
		PayDtoResult.put("timeStamp", timeStamp);
		PayDtoResult.put("nonceStr", nonceStr);
		PayDtoResult.put("package", payDto.getPackage_str());
		PayDtoResult.put("signType", payDto.getSignType());
		PayDtoResult.put("paySign", payDto.getPaySign());

		ForwardViewData viewData = new ForwardViewData();

		viewData.setForwardType(FrontPageForwardType.WEIXIN_JS);
		viewData.setForwardURL(PaymentConstant.WEIXIN_JSAPI_FORWARD_URL);

		viewData.setParams(PayDtoResult);

		return viewData;
	}
}
