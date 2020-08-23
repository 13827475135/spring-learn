package com.nicholas.v2.spring.beans;

/**
 * bean信息封装
 */
public class ZcBeanWrapper {

    private Object wrapperInstance;

    private Class<?> wrapperClass;

    public ZcBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
        this.wrapperClass = wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(Class<?> wrapperClass) {
        this.wrapperClass = wrapperClass;
    }
}
