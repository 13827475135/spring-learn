package com.nicholas.v1.ioc;

import com.nicholas.v1.annotation.ZcService;

@ZcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String getHelloText(String name) {
        return "Hello" + name + ", the message is from HelloService!";
    }
}
