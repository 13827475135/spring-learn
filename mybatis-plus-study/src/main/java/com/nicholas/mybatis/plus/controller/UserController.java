package com.nicholas.mybatis.plus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nicholas.mybatis.plus.entity.User;
import com.nicholas.mybatis.plus.service.TestService;
import com.nicholas.mybatis.plus.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController implements InitializingBean {

    @Autowired
    private UserService userService;

    @Autowired
    private TestService testService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedissonClient redissonClient;

    Cache cache;

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = cacheManager.getCache("user");
    }

    @GetMapping("/test")
    public String test() {
        testService.test();
        userService.saveEntity();
        return "Yeah!";
    }

    @GetMapping("/test-redis")
    public String testRedis() {
        RLock lock = redissonClient.getLock("Nicholas");
        boolean result = false;
        try {
            result = lock.tryLock(5, 50, TimeUnit.SECONDS);
            if (result) {
                Thread.sleep(2000);
                String key = "nicholas";
                String str = cache.get(key, String.class);
                if (StringUtils.isBlank(str)) {
                    cache.put(key, "handsome");
                }
            }
            this.testCache(cache);
            return cache.get("nicholas", String.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (result && lock.isLocked()) {
                lock.unlock();
            }
        }
        return null;
    }

    private void testCache(Cache cache) {
//        User cacheUser = cache.get("single", User.class);
//        Set<User> cacheList = cache.get("list", Set.class);

        User user = new User();
        user.setMobile("3254254");
        user.setName("nicholas");
        user.setSex(1);
        Set<User> set = new HashSet<>();
        set.add(user);
        set.add(user);

        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user);

        Map<String, Object> map = new HashMap<>();
        map.put("nicholas", "yes");
        map.put("user", user);
        map.put("set", set);
        map.put("list", list);

        cache.put("single", user);
        cache.put("set", set);
        cache.put("list", list);
        cache.put("map", map);
    }

    @GetMapping("/test-redis2")
    public String testRedis2() {
        RLock lock = redissonClient.getLock("Nicholas");
        boolean result = false;
        try {
            result = lock.tryLock(5, TimeUnit.SECONDS);
            if (result) {
                System.out.println("成功获取分布式锁");
                return cache.get("nicholas", String.class);
            }
           return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (result && lock.isLocked()) {
                lock.unlock();
            }
        }
        return null;
    }

    @GetMapping("/test-redis3")
    public String testRedis3() {
        RLock lock = redissonClient.getLock("Nicholas");
        boolean result = false;
        try {
            result = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (result) {
                System.out.println("成功获取分布式锁");
            }
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (result && lock.isLocked()) {
                //lock.unlock();
            }
        }
        return null;
    }

    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/test-json")
    public byte[] testJson() throws Exception {
        User user = new User();
        user.setMobile("3254254");
        user.setName("nicholas");
        user.setSex(1);
        byte[] bytes = objectMapper.writeValueAsBytes(user);
        System.out.println(new String(bytes));

        Jackson2JsonRedisSerializer sr = new Jackson2JsonRedisSerializer(User.class);
        System.out.println(new String(sr.serialize(user)));
        return bytes;
    }
}
