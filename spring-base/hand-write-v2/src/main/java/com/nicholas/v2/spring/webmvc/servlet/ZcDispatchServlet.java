package com.nicholas.v2.spring.webmvc.servlet;

import com.nicholas.v2.spring.annotation.ZcController;
import com.nicholas.v2.spring.annotation.ZcRequestMapping;
import com.nicholas.v2.spring.annotation.ZcRequestParam;
import com.nicholas.v2.spring.beans.ZcBeanWrapper;
import com.nicholas.v2.spring.context.ZcApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 30个类手写IoC DI 功能
 * 职责：负责任务调度分发，即委派
 */
public class ZcDispatchServlet extends HttpServlet {

    private List<ZcHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<ZcHandlerMapping, ZcHandlerAdapter> handlerAdapterMap = new HashMap<>();

    private List<ZcViewResolver> viewResolvers = new ArrayList<>();

    private ZcApplicationContext applicationContext;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 6.委派
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                processDispatchResult(req, resp, new ZcModelAndView("500"));
            } catch (Exception e1) {
                e1.printStackTrace();
                resp.getWriter().write("500 Exception,Detail : " + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        applicationContext = new ZcApplicationContext(config.getInitParameter("contextConfigurationLocation"));

        // 初始化MVC九大组件（本例中只写三种）
        initStrategies(applicationContext);

    }

    private void initStrategies(ZcApplicationContext applicationContext) {
        //        //多文件上传的组件
//        initMultipartResolver(context);
//        //初始化本地语言环境
//        initLocaleResolver(context);
//        //初始化模板处理器
//        initThemeResolver(context);
        //handlerMapping
        initHandlerMappings(applicationContext);
        //初始化参数适配器
        initHandlerAdapters(applicationContext);
//        //初始化异常拦截器
//        initHandlerExceptionResolvers(context);
//        //初始化视图预处理器
//        initRequestToViewNameTranslator(context);
        //初始化视图转换器
        initViewResolvers(applicationContext);
//        //FlashMap管理器
//        initFlashMapManager(context);
    }

    private void initHandlerMappings(ZcApplicationContext applicationContext) {
        for(String beanName : applicationContext.getBeanDefinitionNames()) {
            Class<?> clazz = applicationContext.getBean(beanName).getClass();
            if (!clazz.isAnnotationPresent(ZcController.class)) {
                continue;
            }
            String baseUrl = clazz.getAnnotation(ZcController.class).value();
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(ZcRequestMapping.class)) {
                    continue;
                }
                ZcRequestMapping requestMapping = method.getAnnotation(ZcRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                try {
                    Pattern pattern = Pattern.compile(url);
                    ZcHandlerMapping handlerMapping = new ZcHandlerMapping(pattern, method, applicationContext.getBean(beanName));
                    handlerMappings.add(handlerMapping);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initHandlerAdapters(ZcApplicationContext applicationContext) {
        this.handlerMappings.forEach(handler -> {
            this.handlerAdapterMap.put(handler, new ZcHandlerAdapter());
        });
    }

    private void initViewResolvers(ZcApplicationContext applicationContext) {
        String templateRoot = applicationContext.getConfig().getProperty("template.root");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ZcViewResolver(templateRoot));
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ZcHandlerMapping handlerMapping = getHandler(req);
        ZcHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);
        if (handlerMapping == null) {
            processDispatchResult(req, resp, new ZcModelAndView("404"));
            return ;
        }
        ZcModelAndView mv = handlerAdapter.handle(req, resp, handlerMapping);
        processDispatchResult(req, resp, mv);
    }

    private ZcHandlerMapping getHandler(HttpServletRequest request) {
        if(this.handlerMappings.isEmpty()){return  null;}
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        for (ZcHandlerMapping mapping : handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return mapping;
        }
        return null;
    }

    private ZcHandlerAdapter getHandlerAdapter(ZcHandlerMapping handlerMapping) {
        if (this.handlerAdapterMap.isEmpty()) {
            return null;
        }
        return handlerAdapterMap.get(handlerMapping);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ZcModelAndView mv) throws Exception {
        if (null == mv) {
            return ;
        }
        for (ZcViewResolver viewResolver : this.viewResolvers) {
            ZcView view = viewResolver.resolveViewName(mv.getViewName());
            view.render(mv.getModel(), req, resp);
            return ;
        }
    }
}
