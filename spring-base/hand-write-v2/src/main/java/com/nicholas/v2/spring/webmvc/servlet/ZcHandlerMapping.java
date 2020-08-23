package com.nicholas.v2.spring.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class ZcHandlerMapping {

    private Pattern pattern; // 对应的url正则

    private Method method; // 对应的控制器方法

    private Object controller; // 对应的控制器类

    public ZcHandlerMapping(Pattern pattern, Method method, Object controller) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }
}
