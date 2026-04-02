package com.hmdp.utils;

import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class CacheClient {
    private  final StringRedisTemplate stringRedisTemplate;
    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
//    将任意java对象序列化为json并存储在string类型的key中 可以设置TTl过期时间
    public  void set(String key, Object value, Long time, TimeUnit timeUnit) {
stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,timeUnit);
    }
//    设置逻辑过期时间
    public  void setLogicalTime(String key, Object value, Long time, TimeUnit timeUnit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }
}
