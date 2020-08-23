package com.nicholas.v2.spring.context;

import com.nicholas.v2.spring.annotation.ZcAutowired;
import com.nicholas.v2.spring.aop.config.ZcAopConfig;
import com.nicholas.v2.spring.aop.support.JdkDynamicAopProxy;
import com.nicholas.v2.spring.aop.support.ZcAdvisedSupport;
import com.nicholas.v2.spring.beans.ZcBeanWrapper;
import com.nicholas.v2.spring.beans.config.ZcBeanDefinition;
import com.nicholas.v2.spring.beans.support.ZcBeanDefinitionReader;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 职责：完成bean的创建和DI
 */
public class ZcApplicationContext {

    private ZcBeanDefinitionReader reader;

    // BeanDefinition缓存
    private Map<String, ZcBeanDefinition> beanDefinitionMap = new HashMap<>();

    // bean wrapper信息
    private Map<String, ZcBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    public ZcApplicationContext(String... configLoactions) {

        // 1.读取配置文件
        reader = new ZcBeanDefinitionReader(configLoactions);


        // 2.解析配置文件，封装成BeanDefinition
        List<ZcBeanDefinition> beanDefinitions = reader.loadBeanDefinition();

        // 3.把对应的BeanDefinition缓存起来
        doRegisterBeanDefinition(beanDefinitions);

        // 4.实例化与DI操作
        doAutowired();
        doAutowired();
    }

    private void doRegisterBeanDefinition(List<ZcBeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(beanDefinition -> {
            if (!this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            }
        });
    }

    private void doAutowired() {
        //调用getBean()
        //这一步，所有的Bean并没有真正的实例化，还只是配置阶段
        for (Map.Entry<String, ZcBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    // bean的实例化，DI是从这个方法开始的
    public Object getBean(String beanName) {
        // 1.拿到bean的配置信息
        ZcBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        // 2.反射实例化bean
        Object bean = initInstanceBean(beanName, beanDefinition);
        // 3.封装成一个beanWrapper
        ZcBeanWrapper beanWrapper = new ZcBeanWrapper(bean);
        // 4.保存到IoC容器
        factoryBeanInstanceCache.put(beanName, beanWrapper);

        //5.执行DI 注入
        populateBean(beanName, beanDefinition, beanWrapper);
        return bean;
    }

    // 创建实例对象
    private Object initInstanceBean(String beanName, ZcBeanDefinition beanDefinition) {
        if (this.factoryBeanObjectCache.containsKey(beanName)) {
            return factoryBeanObjectCache.get(beanName);
        }
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        Class clazz = null;
        try {
            clazz = Class.forName(className);
            instance = clazz.newInstance();

            // -------------AOP开始----------------------

            //如果满足条件，就直接返回Proxy对象
            //1、加载AOP的配置文件
            ZcAdvisedSupport config = initAopConfig(beanDefinition);
            config.setTargetClass(clazz);
            config.setTarget(instance);

            //判断规则，要不要生成代理类，如果要就覆盖原生对象
            //如果不要就不做任何处理，返回原生对象
            if(config.pointCutMath()){
                instance = new JdkDynamicAopProxy(config).getProxy();
            }

            // -------------AOP结束----------------------

            this.factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    private ZcAdvisedSupport initAopConfig(ZcBeanDefinition beanDefinition) {
        ZcAopConfig aopConfig = new ZcAopConfig();
        aopConfig.setPointCut(this.reader.getContextConfig().getProperty("pointCut"));
        aopConfig.setAspectClass(this.reader.getContextConfig().getProperty("aspectClass"));
        aopConfig.setAspectBefore(this.reader.getContextConfig().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(this.reader.getContextConfig().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrow(this.reader.getContextConfig().getProperty("aspectAfterThrow"));
        aopConfig.setAspectAfterThrowingName(this.reader.getContextConfig().getProperty("aspectAfterThrowingName"));
        return new ZcAdvisedSupport(aopConfig);
    }


    // DI操作，可能涉及到循环依赖，可以用两个缓存，循环两次，把第一次读取结果为空的BeanDefinition存到缓存
    // 第一次循环后，第二次循环再检查第一次的缓存，再进行赋值
    private void populateBean(String beanName,  ZcBeanDefinition beanDefinition, ZcBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        Class clazz = beanWrapper.getWrapperClass();
        if (beanWrapper.getWrapperInstance() instanceof Proxy) {
            instance = ((JdkDynamicAopProxy)Proxy.getInvocationHandler((Proxy)beanWrapper.getWrapperInstance())).getAdviceSupport().getTarget();
            clazz = ((JdkDynamicAopProxy)Proxy.getInvocationHandler((Proxy)beanWrapper.getWrapperInstance())).getAdviceSupport().getTargetClass();
        } else {
            instance = beanWrapper.getWrapperInstance();
            clazz = beanWrapper.getWrapperClass();
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ZcAutowired.class)) {
                continue;
            }
            ZcAutowired zcAutowired = field.getAnnotation(ZcAutowired.class);
            String autowiredBeanName = zcAutowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            // 对private强吻
            field.setAccessible(true);

            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public Properties getConfig() {
        return this.reader.getContextConfig();
    }

}
