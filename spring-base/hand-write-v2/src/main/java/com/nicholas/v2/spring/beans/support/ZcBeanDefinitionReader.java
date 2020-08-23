package com.nicholas.v2.spring.beans.support;

import com.nicholas.v2.spring.annotation.ZcController;
import com.nicholas.v2.spring.annotation.ZcService;
import com.nicholas.v2.spring.beans.config.ZcBeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 *
 */
public class ZcBeanDefinitionReader {

    // 配置文件路径
    private String[] configLocations;

    // 配置文件属性
    private Properties contextConfig = new Properties();

    // 类名缓存
    private List<String> registryBeanNames = new ArrayList<>();

    public ZcBeanDefinitionReader(String... configLoactions) {
        this.configLocations = configLoactions;
        doLoadConfig(configLoactions[0]);
        doScan(contextConfig.getProperty("scanPackage"));
    }

    public List<ZcBeanDefinition> loadBeanDefinition() {
        List<ZcBeanDefinition> result = new ArrayList<>();
        registryBeanNames.forEach(className -> {
            try {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.getAnnotation(ZcController.class) != null || beanClass.getAnnotation(ZcService.class) != null) {
                    // 保存类对应的全类名，还有beanName
                    // 1.默认类名首字母小写
                    String beanName = toLowerFirstCase(beanClass.getSimpleName());
                    // 2.自定义名称
                    // 3.接口注入
                    for(Class<?> iClazz : beanClass.getInterfaces()) {
                        result.add(doCreateBeanDefinition(iClazz.getName(), beanClass.getName()));
                    }
                    result.add(doCreateBeanDefinition(beanName, beanClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    private void doLoadConfig(String configLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocation.replaceAll("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doScan(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScan(scanPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().replace(".class", "");
                registryBeanNames.add(scanPackage + "." + className);
            }
        }
    }

    private ZcBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {
        ZcBeanDefinition beanDefinition = new ZcBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(beanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getContextConfig() {
        return contextConfig;
    }
}
