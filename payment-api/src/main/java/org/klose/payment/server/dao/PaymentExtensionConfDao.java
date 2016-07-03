package org.klose.payment.server.dao;

import org.klose.payment.server.po.PaymentExtensionConfPO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PaymentExtensionConfDao extends CrudRepository<PaymentExtensionConfPO, Long> {
	@Query("from org.klose.payment.server.po.PaymentExtensionConfPO pec " +
			" where pec.accountType = :accountType and pec.bizType = :bizType")
	PaymentExtensionConfPO findByBizTypeAndAccountType(@Param("accountType") Integer accountType, @Param("bizType") String bizType);

}
