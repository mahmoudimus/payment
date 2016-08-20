package org.klose.payment.service.notification.impl;


import org.klose.payment.api.CallBackAgent;
import org.klose.payment.bo.PaymentResult;
import org.klose.payment.common.context.ApplicationContextUtils;
import org.klose.payment.constant.PaymentStatus;
import org.klose.payment.dao.TransactionDao;
import org.klose.payment.po.TransactionPO;
import org.klose.payment.service.notification.ProcessNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessNotificationServiceImpl implements
        ProcessNotificationService {

    private static final int WORKER_THREAD_POOL_SIZE = 4;

    private ExecutorService threadPool;

    @PostConstruct
    public void init() {
        logger.info("Async Payment-service notification Worker Thread Pool Initialized!");
        threadPool = Executors.newFixedThreadPool(WORKER_THREAD_POOL_SIZE);
    }

    @PreDestroy
    public void cleanUp()  {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(45, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();

                if (!threadPool.awaitTermination(45, TimeUnit.SECONDS))
                    logger.error("Async Payment notification Worker Thread Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Autowired
    private TransactionDao transactionDao;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handlePaymentCallback(String orderNo, String notifyDatagram,
                                      String payId, String notifyMessage, Map<String, String> notifyParams) {

        TransactionPO transaction = updateTransactionData(
                orderNo, notifyDatagram, payId, notifyMessage);

        if (transaction != null) {
            notifyCallbackAgent(transaction, notifyMessage, notifyParams);
        }
    }

    @Override
    public void handlePaymentCallback(Long transactionId, String notifyDatagram,
                                      String payId, String notifyMessage, Map<String, String> notifyParams) {
        TransactionPO transaction = updateTransactionData(
                transactionId, notifyDatagram, payId, notifyMessage);

        if (transaction != null) {
            notifyCallbackAgent(transaction, notifyMessage, notifyParams);
        }
    }

    @Transactional
    private TransactionPO updateTransactionData(String orderNo,
                                                String notifyDatagram, String payId, String notifyMessage) {
        TransactionPO transaction = transactionDao.findByOrderNoOnLock(orderNo).get(0);
        return doUpdateTransaction(transaction, notifyDatagram, payId, notifyMessage);
    }

    @Transactional
    private TransactionPO updateTransactionData(Long transactionId,
                                                String notifyDatagram, String payId, String notifyMessage) {
        TransactionPO transaction = transactionDao.findOneOnLock(transactionId);
        return doUpdateTransaction(transaction, notifyDatagram, payId, notifyMessage);
    }

    private TransactionPO doUpdateTransaction(TransactionPO transaction, String noitifyDatagram, String payId, String notifyMessage) {
        if (transaction != null &&
                PaymentStatus.Proceed.getStatusId() == transaction.getStatus()) {

            String messageLog = transaction.getMessageLog() != null ? transaction
                    .getMessageLog() : "";

            transaction.setMessageLog(messageLog.concat(noitifyDatagram));
            transaction.setCompletion_time(new Date());
            transaction.setNotificationMsg(notifyMessage);

            if (payId != null && !payId.isEmpty()) {
                transaction.setPayId(payId);
                transaction.setStatus(PaymentStatus.Success.getStatusId());
            } else {
                transaction.setStatus(PaymentStatus.Failed.getStatusId());
            }

            transactionDao.save(transaction);

            return transaction;
        } else {
            return null;
        }
    }

    @Transactional
    private void notifyCallbackAgent(TransactionPO transaction,
                                     String notifyMessage, Map<String, String> notifyParams) {

        CallBackAgent agent = ApplicationContextUtils.getBean(
                transaction.getCallBackAgent(), CallBackAgent.class);

        if (agent != null) {
            logger.info("Notify notification agent: ");

            PaymentResult paymentResult = new PaymentResult();

            paymentResult.setOrderNo(transaction.getTransactionNo());
            paymentResult.setPayId(transaction.getPayId());
            paymentResult.setAmount(transaction.getAmount());
            paymentResult.setCompletionTime(transaction.getCompletion_time());
            paymentResult.setStatus(PaymentStatus.valueOf(transaction
                    .getStatus()));
            paymentResult.setErrorMsg(notifyMessage);
            paymentResult.setTransactionId(transaction.getId());

            paymentResult.setExtData(notifyParams);

            agent.processPaymentCallback(paymentResult);

            if (transaction.getStatus() == PaymentStatus.Success.getStatusId()) {
                transaction.setStatus(PaymentStatus.Completed.getStatusId());
                transactionDao.save(transaction);
            }
        }
    }
}
