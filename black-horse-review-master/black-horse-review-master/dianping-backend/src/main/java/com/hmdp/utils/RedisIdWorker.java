package com.hmdp.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    public static StringRedisTemplate stringRedisTemplate;

    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
private  final  Long BEGIN_TIME=1767225600L;
private  final  int  LEFT_MOVE=32;
    public long nextId(String keyPrefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        Long time=nowSecond - BEGIN_TIME;
        String  data= now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        Long incremented = stringRedisTemplate.opsForValue().increment("irc" +":"+ keyPrefix +":"+ data);
        return time << LEFT_MOVE | incremented;
    }


}
