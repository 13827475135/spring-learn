package com.nicholas.v2.spring.webmvc.servlet;

import java.util.Map;

public class ZcModelAndView {

    private String viewName;

    private Map<String, Object> model;

    public ZcModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ZcModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
