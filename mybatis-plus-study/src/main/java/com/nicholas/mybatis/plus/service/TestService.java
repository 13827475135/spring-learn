package com.nicholas.mybatis.plus.service;

import com.nicholas.mybatis.plus.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    UserMapper userMapper;

    public void test() {
        System.out.println();
    }
}
