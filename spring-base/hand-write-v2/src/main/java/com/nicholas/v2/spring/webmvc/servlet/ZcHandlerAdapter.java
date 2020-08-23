package com.nicholas.v2.spring.webmvc.servlet;

import com.nicholas.v2.spring.annotation.ZcRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ZcHandlerAdapter {

    private Object handler;

    public boolean support() {
        return false;
    }


    public ZcModelAndView handle(HttpServletRequest request, HttpServletResponse response, ZcHandlerMapping handler) {

        //保存形参列表
        //将参数名称和参数的位置，这种关系保存起来
        Map<String, Integer> paramIndexMapping = new HashMap<>();
        Annotation[] [] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof ZcRequestParam){
                    String paramName = ((ZcRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //处理 request response
        Class<?> [] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> parameterType = paramTypes[i];
            if(parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
                paramIndexMapping.put(parameterType.getName(),i);
            }
        }

        Map<String, String[]> params = request.getParameterMap();

        Object [] paramValues = new Object[paramTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s+",",");

            if(!paramIndexMapping.containsKey(param.getKey())){
                continue;
            }

            int index = paramIndexMapping.get(param.getKey());

            //允许自定义的类型转换器Converter
            paramValues[index] = castStringValue(value,paramTypes[index]);
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = request;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = response;
        }

        Object result = null;
        try {
            result = handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(result == null || result instanceof Void){
            return null;
        }
        boolean isModelAndView = handler.getMethod().getReturnType() == ZcModelAndView.class;
        if(isModelAndView){
            return (ZcModelAndView) result;
        }
        return null;
    }

    private Object castStringValue(String value, Class<?> paramType) {
        if(String.class == paramType){
            return value;
        }else if(Integer.class == paramType){
            return Integer.valueOf(value);
        }else if(Double.class == paramType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }

    }


}
