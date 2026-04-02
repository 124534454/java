package com.hmdp.service.impl;

import cn.hutool.core.lang.UUID;
import com.hmdp.utils.ILock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {
    private String name;
    private StringRedisTemplate stringRedisTemplate;
    private static  final String ID_LOCK = UUID.fastUUID().toString(true) + "-";
    private static  final String LOCK_KEY = "lock:";
    private  static  final DefaultRedisScript<Long> UNLOCK_SCRIPT;
    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }
    static {
        UNLOCK_SCRIPT=new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }
    @Override
    public boolean tryLock(Long timeoutSec) {
        String lockid = ID_LOCK + Thread.currentThread().getId();

        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_KEY + name, lockid, timeoutSec, TimeUnit.SECONDS);
        System.out.println(success);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void delLock() {
                String lockid = ID_LOCK + Thread.currentThread().getId();
        String s = stringRedisTemplate.opsForValue().get(LOCK_KEY + name);
        System.out.println(lockid+"---"+s);
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(LOCK_KEY + name),ID_LOCK + Thread.currentThread().getId());

//        获取的线程id

//获取存储的锁
//
//        if(lockid.equals(s)){
//            stringRedisTemplate.delete(LOCK_KEY + name);
//
//        }
    }
}
