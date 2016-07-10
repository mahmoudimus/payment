package org.klose.payment.service.callback;

import java.util.Map;

public interface ProcessNotificationService {

	/**
	 * 
	 * @param orderNo
	 * @param notifyDatagram
	 * @param payId
	 * @param notifyMessage
	 * @param notifyParams
	 */
	void handlePaymentCallback(
			String orderNo,
			String notifyDatagram,
			String payId,
			String notifyMessage,
			Map<String, String> notifyParams);

	/**
	 *
	 * @param transactionId
	 * @param notifyDatagram
	 * @param payId
	 * @param notifyMessage
	 * @param notifyParams
	 */
	void handlePaymentCallback(
			Long transactionId,
			String notifyDatagram,
			String payId,
			String notifyMessage,
			Map<String, String> notifyParams);

}
