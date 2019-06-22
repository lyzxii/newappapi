package com.caiyi.lottery.tradesystem;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanFactoryUtils implements ApplicationContextAware {

	private static ApplicationContext appCtx;

    /**
     * 重写setApplicationContext方法
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。
     *
     * @param applicationContext ApplicationContext 对象.
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanFactoryUtils.appCtx = applicationContext;
    }

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * 这是一个便利的方法，帮助我们快速得到一个BEAN
     *
     * @param beanName bean的名字
     * @return 返回一个bean对象
     */
    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }
    
    /** 
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. 
     */  
    @SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {  
        return (T) appCtx.getBeansOfType(clazz);  
    }  
}
