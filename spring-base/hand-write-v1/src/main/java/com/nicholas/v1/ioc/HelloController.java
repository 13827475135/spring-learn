package com.nicholas.v1.ioc;

import com.nicholas.v1.annotation.ZcAutowired;
import com.nicholas.v1.annotation.ZcController;
import com.nicholas.v1.annotation.ZcRequestMapping;
import com.nicholas.v1.annotation.ZcRequestParam;

@ZcController("/demo")
public class HelloController {


    @ZcAutowired
    private HelloService helloService;

    @ZcRequestMapping("/hello")
    public String testHello(@ZcRequestParam String name) {
        return "Hello, " + name + " is handsome";
    }

    @ZcRequestMapping("/hello2")
    public String testHello2(@ZcRequestParam String name) {
        return helloService.getHelloText(name);
    }

}
