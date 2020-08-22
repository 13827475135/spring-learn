package com.nicholas.mybatis.plus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nicholas.mybatis.plus.entity.LoginHistory;
import com.nicholas.mybatis.plus.entity.User;
import com.nicholas.mybatis.plus.mapper.LoginHistoryMapper;
import com.nicholas.mybatis.plus.mapper.UserMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, InitializingBean {

    @Autowired
    private LoginHistoryMapper loginHistoryMapper;

    @Override
    @Transactional
    public void afterPropertiesSet() throws Exception {
        System.out.println();
        /*baseMapper.selectById(1L);
        baseMapper.selectById(1L);*/
    }

    @Override
    @Transactional
    public void saveEntity() {
        User user = new User();
        user.setMobile("13827475135");
        user.setSex(1);
        user.setName("大厦");
        baseMapper.insert(user);
    }




    @Override
    @Transactional
    public void mysqlRepeatedTestOne(long id, long time) {
        log.info("进入方法一");
        log.info("方法一第一次查询");
        //User user222 = baseMapper.selectById(id - 1);
        LoginHistory history = loginHistoryMapper.selectById(2);
        User user = null;
        log.info("第一次查询结果：" + (user == null? null : user.toString()));
        try {
            log.info("方法一睡眠等待");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("方法一睡眠结束，开始第二次查询");
        user = baseMapper.selectById(id);
        log.info("第二次查询结果：" + (user == null? null : user.toString()));
        log.info("退出方法一");
    }

    @Override
    @Transactional
    public void mysqlRepeatedTestTwo() {
        log.info("进入方法二，开始插入数据");
        User user = new User();
        user.setMobile("15999999998");
        user.setSex(1);
        user.setName("CFCA");
        baseMapper.insert(user);
        log.info("方法二返回的id值为：" + user.getId());
        log.info("退出方法二");
    }

    @Override
    @Transactional
    public void mysqlLinjianLockTest(long id, long time) {
        /**
         * 假设数据库id最大为M，select * from user where id > (M-x) and id <= M 会把左开右闭区间(M-x, M]和下一个区间(M, 无穷大)
         * 锁定第一个区间好理解，防止
         */
        log.info("MySQL临键锁测试");
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(User::getId, id - 2);
        wrapper.le(User::getId, id);
        List<User> userList = baseMapper.selectList(wrapper);
        log.info("First User List:" + userList);
        try {
            log.info("睡眠等待");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userList = baseMapper.selectList(wrapper);
        log.info("Second User List:" + userList);
    }

    @Override
    @Transactional
    public void mysqlTransactionTest(long id, long time) {
        log.info("进入事务测试，应该已开启事务");
        LoginHistory history = loginHistoryMapper.selectById(2);
        User user = null;
        log.info("第一次查询结果：" + (user == null? null : user.toString()));
        try {
            log.info("睡眠等待");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        user = baseMapper.selectById(id);
        log.info("第二次查询结果：" + (user == null? null : user.toString()));
        log.info("退出事务测试");
    }

    @Override
    @Transactional
    public void mysqlTransactionTest2(long id) {
        log.info("开始修改数据");
        User user = baseMapper.selectById(id);
        user.setName(user.getName() + System.currentTimeMillis());
        baseMapper.updateById(user);
        log.info("修改成功，退出");
    }

    @Override
    @Transactional
    public void mysqlUpdateTest(long id, long time) {
        log.info("One 修改数据Start");
        User user = baseMapper.selectById(id);
        user.setName(user.getName() + "-ONE-" +System.currentTimeMillis());
        try {
            log.info("睡眠等待");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        baseMapper.updateById(user);
        log.info("One 修改数据end");
    }

    @Override
    @Transactional
    public void mysqlUpdateTest2(long id) {
        User user = baseMapper.selectById(id);
        log.info("Two 修改数据Start");
        user.setName(user.getName() + "-TWO-" +System.currentTimeMillis());
        baseMapper.updateById(user);
        log.info("Two 修改数据end");
    }
}
