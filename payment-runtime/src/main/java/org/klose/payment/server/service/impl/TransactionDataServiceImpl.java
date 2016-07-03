/**
 *
 */
package org.klose.payment.server.service.impl;

import org.klose.payment.server.bo.BillingData;
import org.klose.payment.server.bo.PaymentResult;
import org.klose.payment.server.constant.PaymentConstant;
import org.klose.payment.server.constant.PaymentStatus;
import org.klose.payment.server.dao.TransactionDao;

import org.klose.payment.server.po.TransactionPO;
import org.klose.payment.server.service.TransactionDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gary.chang
 */
@Service
public class TransactionDataServiceImpl implements TransactionDataService {

    @Autowired
    TransactionDao transactionDao;

    @Override
    public PaymentResult findPaymentByOrderNo(String orderNo) {
        PaymentResult result = null;

        if (orderNo != null && !orderNo.isEmpty()) {
            TransactionPO po = transactionDao.findByTransactionNo(orderNo);
            result = convertToPaymentResult(po);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ebao.insurance.paymentservice.service.TransactionDataService#
     * findPaymentByTransactionId(java.lang.Long)
     */
    @Override
    public PaymentResult findPaymentByTransactionId(Long transId) {
        PaymentResult result = null;

        if (transId != null) {
            TransactionPO po = transactionDao.findOne(transId);
            result = convertToPaymentResult(po);
        }

        return result;
    }

    private PaymentResult convertToPaymentResult(TransactionPO po) {
        PaymentResult result = null;

        if (po != null) {
            result = new PaymentResult();

            result.setTransactionId(po.getId());
            result.setOrderNo(po.getTransactionNo());
            result.setBizNo(po.getBizNo());
            result.setAmount(po.getAmount());
            result.setCompletionTime(po.getCompletion_time());
            result.setPayId(po.getPayId());
            result.setStatus(PaymentStatus.valueOf(po.getStatus()));
            result.setErrorMsg(po.getNotificationMsg());
            result.addExtData(PaymentConstant.KEY_PAYMENT_RETURN_URL,
                    po.getReturnURL());
        }

        return result;
    }

    @Override
    public Long createTransactionFromBillingData(BillingData bill) {
        TransactionPO transaction = new TransactionPO();

        transaction.setAccountNo(bill.getAccountNo());
        transaction.setAmount(bill.getPrice().multiply(
                new BigDecimal(bill.getQuantity())));
        transaction.setCallBackAgent(bill.getCallBackAgent());
        transaction.setCreation_time(new Date());
        transaction.setCurrency(bill.getCurrency());
        // @TODO
        transaction.setTransactionNo(
                bill.getOrderNo() != null ? bill.getOrderNo() : bill.getBizNo());

        transaction.setBizNo(bill.getBizNo());
        transaction.setStatus(PaymentStatus.Proceed.getStatusId());
        transaction.setSubject(bill.getSubject());
        transaction.setReturnURL(bill.getReturnURL());

        if (bill.getExtData() != null
                && bill.getExtData().containsKey(
                PaymentConstant.KEY_WEIXIN_PREPAY_ID)) {
            transaction.setPrePayId((String) (bill.getExtData()
                    .get(PaymentConstant.KEY_WEIXIN_PREPAY_ID)));
        }

        return transactionDao.save(transaction).getId();
    }

    @Override
    public String processReturnURL(Long transId, String returnURL) {
        if (transId == null || returnURL == null) {
            return null;
        }

        String result = returnURL.concat(
                String.format(PaymentConstant.SUFFIX_RETURN_URL, transId.toString()));

        TransactionPO entity = transactionDao.findOne(transId);
        entity.setReturnURL(result);
        transactionDao.save(entity);

        return result;
    }

    @Override
    public PaymentResult findSuccessfulPaymentByBizNo(String bizNo) {
        PaymentResult result = null;

        if (bizNo != null) {
            TransactionPO po = transactionDao.findPaidTransactionByBizNo(bizNo);
            result = convertToPaymentResult(po);
        }

        return result;
    }

    @Override
    public void updatePaymentResult(
            Long transId, boolean isSuccess, String payId, String notifyMsg) {

        if (transId != null) {
            TransactionPO entity = transactionDao.findOne(transId);

            entity.setStatus(isSuccess ?
                    PaymentStatus.Success.getStatusId() :
                    PaymentStatus.Failed.getStatusId());

            entity.setPayId(payId != null ? payId : "");

            entity.setCompletion_time(new Date());
            entity.setNotificationMsg(notifyMsg);

            transactionDao.save(entity);
        }

        return;
    }
}
