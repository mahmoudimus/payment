package org.klose.payment.common.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		applicationContext = context;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public static <T> T getBean(String key, Class<T> cls) {
		return applicationContext.getBean(key, cls);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String key, Class<T> cls, Object... args) {
		Object result = applicationContext.getBean(key, args);
		if(result == null){
			result = applicationContext.getBean(cls, args);
		}
		if(result == null){
			return null;
		}
		return (T)result;
	}
	
	/**
	 * The bean id name is same as the class name.
	 * @param <T>
	 * @param cls
	 * @return <T>
	 */
	public static <T> T getBean(Class<T> cls) {
		return applicationContext.getBean(cls);
	}
}