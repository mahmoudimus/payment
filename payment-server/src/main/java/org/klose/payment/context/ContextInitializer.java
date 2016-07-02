package org.klose.payment.context;

/**
 * Created by klose on 7/2/16.
 */
import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


public class ContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory
            .getLogger(ContextInitializer.class);

    public void initialize(ConfigurableApplicationContext applicationContext) {
        logger.debug("Context is initialized");
        BeanUtilsBean.getInstance().
                getConvertUtils().register(new LongConverter(null),
                Long.class);
        BeanUtilsBean.getInstance()
                .getConvertUtils().register(new BigDecimalConverter(null),
                BigDecimal.class);
    }

}
