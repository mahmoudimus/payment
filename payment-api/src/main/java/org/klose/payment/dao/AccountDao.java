package org.klose.payment.dao;

import org.klose.payment.po.AccountPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountDao extends CrudRepository<AccountPO, Long>{
	
	@Query("select acc.accountNo, acc.name, acc.merchantNo, acc.merchantName, acc.statusId, acc.type, acc.useType " +
			" from com.ebao.insurance.paymentservice.po.AccountPO acc " +
			" where acc.type = :type and acc.useType = 1 and acc.statusId = 1")
	List<Object[]> findByPaymentType(@Param("type") Integer type);
	
	@Query("select acc.accountNo, acc.name, acc.merchantNo, acc.merchantName, acc.statusId, acc.type, acc.useType  " +
			" from com.ebao.insurance.paymentservice.po.AccountPO acc " +
			" where acc.useType = 1 and acc.statusId = 1")
	List<Object[]> getAllEffectiveAccounts();
	
	AccountPO findByAccountNo(String accountNo);
}
