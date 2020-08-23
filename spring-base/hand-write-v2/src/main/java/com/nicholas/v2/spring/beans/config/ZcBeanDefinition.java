package com.nicholas.v2.spring.beans.config;

/**
 * Bean的定义类
 */
public class ZcBeanDefinition {

    // bean的类名
    private String beanClassName;
    // bean的名称
    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
