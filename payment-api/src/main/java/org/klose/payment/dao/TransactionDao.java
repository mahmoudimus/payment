package org.klose.payment.dao;

import org.klose.payment.po.TransactionPO;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface TransactionDao extends CrudRepository<TransactionPO, Long> {
	
	TransactionPO findByTransactionNo(String orderNo);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM #{#entityName} m WHERE m.id=:transactionId")
	TransactionPO findOneOnLock(@Param("transactionId") Long transactionId);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM #{#entityName} m WHERE m.transactionNo=:orderNo")
	List<TransactionPO> findByOrderNoOnLock(@Param("orderNo") String orderNo);
	
	@Query("SELECT trans.returnURL FROM #{#entityName} trans" +
				 " WHERE trans.id=:transactionId")
	String findReturnURLById(@Param("transactionId") Long transactionId);
	
	@Query("SELECT m FROM #{#entityName} m" +
			 " WHERE m.bizNo=:bizNo and (m.status=1 or m.status=-1)")

    TransactionPO findPaidTransactionByBizNo(@Param("bizNo") String bizNo);
	
}
