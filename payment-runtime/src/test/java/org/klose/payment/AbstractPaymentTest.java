package org.klose.payment;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by klose.wu on 7/11/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-payment-test.xml" })
@Transactional(transactionManager = "transactionManager")
@Commit
public abstract class AbstractPaymentTest {
}
