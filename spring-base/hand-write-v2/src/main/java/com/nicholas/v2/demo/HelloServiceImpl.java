package com.nicholas.v2.demo;

import com.nicholas.v2.spring.annotation.ZcAutowired;
import com.nicholas.v2.spring.annotation.ZcService;

@ZcService
public class HelloServiceImpl implements HelloService {

    @ZcAutowired
    private TestService testService;

    @Override
    public String getHelloText(String name) {
        testService.test();
        return "Hello" + name + ", the message is from HelloService!";
    }
}
