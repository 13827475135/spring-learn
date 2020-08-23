package com.nicholas.v2.demo;

import com.nicholas.v2.spring.annotation.ZcService;

@ZcService
public class TestServiceImpl implements TestService{

    @Override
    public void test() {
        System.out.println("Test Service!!!");
    }
}
