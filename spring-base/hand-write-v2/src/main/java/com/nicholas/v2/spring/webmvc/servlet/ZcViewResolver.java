package com.nicholas.v2.spring.webmvc.servlet;

import java.io.File;

public class ZcViewResolver {

    private static final String DEFAULT_VIEW_SUFFIX = ".html";

    private File templateRootDir;

    public ZcViewResolver(String templateRoot) {
        String templatePath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templatePath);
    }

    public ZcView resolveViewName(String viewName) {
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_VIEW_SUFFIX)? viewName : (viewName + DEFAULT_VIEW_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new ZcView(templateFile);
    }
}
