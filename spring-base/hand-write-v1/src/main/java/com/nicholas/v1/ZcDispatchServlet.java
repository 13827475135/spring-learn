package com.nicholas.v1;

import com.nicholas.hand.write.base.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ZcDispatchServlet extends HttpServlet {

    // web.xml配置
    private Properties contextConfig = new Properties();

    // 类名缓存
    private List<String> classNames = new ArrayList<>();

    // IoC容器, key默认类型首字母小写，
    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
        // 6.委派
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Error :" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init();
        // 1.加载配置
        doLoadConfig(config.getInitParameter("contextConfigurationLocation"));

        // 2.扫描包
        doScan(contextConfig.getProperty("scanPackage"));

        // ========= IoC部分 =============
        // 3. 初始化IoC容器，将扫描到的类放入IoC
        doInstance();

        // ========= DI部分 =============
        // 4.完成依赖注入
        doAutowired();

        // 5.MVC部分
        doInitHandleMapping();



    }

    private void doLoadConfig(String configLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocation);
        try {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                classNames.add(scanPackage + "." + className);
            }
        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return ;
        }
        for (String name : classNames) {
            try {
                Class<?> clazz = Class.forName(name);
                if (clazz.isAnnotationPresent(ZcController.class)) {
                    Object instance = clazz.newInstance();
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(ZcService.class)) {
                    // 1.默认首字母小写
                    Object instance = clazz.newInstance();

                    // 2.在多个包下出现相同的类名，只能自己取一个全局唯一的名字
                    String beanName = clazz.getAnnotation(ZcService.class).value();
                    if ("".equals(beanName)) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    ioc.put(beanName, instance);

                    // 3.如果是接口，判断有几个实现类，如果一个，默认选择这个，如果多个，抛异常
                    for (Class<?> i: clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new RuntimeException("Exists name in IoC " + i.getName());
                        }
                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return ;
        }
        ioc.forEach((key, value) -> {
            for (Field field : value.getClass().getFields()) {

                if (!field.isAnnotationPresent(ZcAutowired.class)) {
                    continue;
                }
                ZcAutowired zcAutowired = field.getAnnotation(ZcAutowired.class);
                String beanName = zcAutowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }

                // 对private强吻
                field.setAccessible(true);

                try {
                    field.set(value, ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void doInitHandleMapping() {
        if (ioc.isEmpty()) {
            return ;
        }
        for(Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(ZcController.class)) {
                continue;
            }
            String baseUrl = clazz.getAnnotation(ZcController.class).value();
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(ZcRequestMapping.class)) {
                    continue;
                }
                ZcRequestMapping requestMapping = method.getAnnotation(ZcRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replace("/+", "/");
                handlerMapping.put(url, method);
            }
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contenxPath = req.getContextPath();
        url = url.replaceAll(contenxPath, "");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Url Not Found");
            return ;
        }
        Map<String, String[]> params = req.getParameterMap();

        Method method = this.handlerMapping.get(url);
        Class<?> [] parameterTypes = method.getParameterTypes();
        Object[] paramValues = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class) {
                paramValues[i] = req;
            } else  if (parameterType == HttpServletResponse.class) {
                paramValues[i] = resp;
            } else if (parameterType == String.class) {
                Annotation[] [] pa = method.getParameterAnnotations();
                for (int j = 0; j < pa.length; j++) {
                    for (Annotation annotation : pa[i]) {
                        if (!(annotation instanceof ZcRequestParam)) {
                            continue;
                        }
                        String paramName = ((ZcRequestParam) annotation).value().trim();
                        if (!"".equals(paramName)) {
                            String value = Arrays.toString(params.get(paramName))
                                    .replaceAll("\\[|\\]", "")
                                    .replaceAll("\\s+", ",");
                            paramValues[i] = value;
                        }
                    }
                }
            }
        }
        Class<?> clazz = method.getDeclaringClass();
        String beanName = toLowerFirstCase(clazz.getName());
        method.invoke(ioc.get(beanName), new Object[]{req, resp, params.get("name")});
    }

    private String toLowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
