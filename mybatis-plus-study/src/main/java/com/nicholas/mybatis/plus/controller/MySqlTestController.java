package com.nicholas.mybatis.plus.controller;

import com.nicholas.mybatis.plus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mysql")
public class MySqlTestController {

    @Autowired
    private UserService userService;

    @GetMapping("/one")
    public void testInsertOne(@RequestParam long id, @RequestParam long time) {
        userService.mysqlRepeatedTestOne(id, time);
    }

    @GetMapping("/two")
    public void testInsertTwo() {
        userService.mysqlRepeatedTestTwo();
    }

    @GetMapping("/three")
    public void testInsertThree(@RequestParam long id, @RequestParam long time) {
        userService.mysqlLinjianLockTest(id, time);
    }



    @GetMapping("/four")
    public void testTransaction(@RequestParam long id, @RequestParam long time) {
        userService.mysqlTransactionTest(id, time);
    }

    @GetMapping("/five")
    public void testTransaction2(@RequestParam long id) {
        userService.mysqlTransactionTest2(id);
    }


    @GetMapping("/update1")
    public void testUpdate(@RequestParam long id, @RequestParam long time) {
        userService.mysqlUpdateTest(id, time);
    }

    @GetMapping("/update2")
    public void testUpdate2(@RequestParam long id) {
        userService.mysqlUpdateTest2(id);
    }

}
