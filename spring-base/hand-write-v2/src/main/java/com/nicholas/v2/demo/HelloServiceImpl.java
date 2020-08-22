package com.nicholas.v2.demo;

import com.nicholas.hand.write.base.ZcService;

@ZcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String getHelloText(String name) {
        return "Hello" + name + ", the message is from HelloService!";
    }
}
