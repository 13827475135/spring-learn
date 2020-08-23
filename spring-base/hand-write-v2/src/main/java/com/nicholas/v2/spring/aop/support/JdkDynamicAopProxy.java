package com.nicholas.v2.spring.aop.support;

import com.nicholas.v2.spring.aop.aspect.ZcAdvice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 啥都不说了，切面相关的JDK动态具体代理实现
 */
public class JdkDynamicAopProxy implements InvocationHandler {

    private ZcAdvisedSupport adviceSupport;

    public JdkDynamicAopProxy(ZcAdvisedSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Map<String, ZcAdvice> advices = adviceSupport.getAdvices(method, null);

        Object returnValue;
        try {
            invokeAdvice(advices.get("before"));

            returnValue = method.invoke(this.adviceSupport.getTarget(), args);

            invokeAdvice(advices.get("after"));
        }catch (Exception e){
            invokeAdvice(advices.get("afterThrow"));
            throw e;
        }

        return returnValue;
    }

    private void invokeAdvice(ZcAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader() ,this.adviceSupport.getTargetClass().getInterfaces(), this);
    }

    public ZcAdvisedSupport getAdviceSupport() {
        return adviceSupport;
    }

    public void setAdviceSupport(ZcAdvisedSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }
}
