package com.nicholas.v2.demo;

import com.nicholas.v2.spring.annotation.ZcAutowired;
import com.nicholas.v2.spring.annotation.ZcController;
import com.nicholas.v2.spring.annotation.ZcRequestMapping;
import com.nicholas.v2.spring.annotation.ZcRequestParam;
import com.nicholas.v2.spring.webmvc.servlet.ZcModelAndView;

import java.util.HashMap;
import java.util.Map;

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

    @ZcRequestMapping("/add*.json")
    public ZcModelAndView testMv(@ZcRequestParam String name) {
        String result = helloService.getHelloText(name);
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Nicholas");
        map.put("data", "CXFASDGASDFGADFGASD");
        map.put("token", "gfaQAA6465654d6654GF6Aefaefgaeg");
        ZcModelAndView mv = new ZcModelAndView("first", map);
        return mv;
    }

}
