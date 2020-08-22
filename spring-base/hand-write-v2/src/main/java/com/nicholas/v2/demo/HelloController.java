package com.nicholas.v2.demo;

import com.nicholas.hand.write.base.ZcAutowired;
import com.nicholas.hand.write.base.ZcController;
import com.nicholas.hand.write.base.ZcRequestMapping;
import com.nicholas.hand.write.base.ZcRequestParam;

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
