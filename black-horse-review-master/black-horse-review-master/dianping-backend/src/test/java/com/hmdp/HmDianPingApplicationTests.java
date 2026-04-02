package com.hmdp;

import com.hmdp.service.impl.ShopServiceImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class HmDianPingApplicationTests {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private ShopServiceImpl shopService;

    @Test
    void setShopService() {
        shopService.saveShop1Redis(1L, 20L);
        for (Long i = 2L; i <= 14; i++) {
            shopService.saveShop1Redis(i, 20L);

        }
    }

    @Test
    void setShopService2() {
        String[] strs = new String[10000];
        int j = 0;

        for (int i = 0; i < 10000000; i++) {
            String uv = "dau" + i;
            j = i % 10000;
            strs[j] = i + "";

        }

    }

    @Test
    void setShopService3() {

        String key = "deu" + LocalDate.now();
        for (int i = 0; i < 10000; i++) {
            stringRedisTemplate.opsForValue().setBit(key, i, true);

        }
        RedisCallback<Long> redisCallback = new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(key.getBytes());
            }
        };
        Long execute = stringRedisTemplate.execute(redisCallback);
        System.out.println(execute);


    }

    @Test
    void setShopService4() {
//        计算月活量
        List<String> dayKeys = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();
//        获取当前月总共多少天
        int lengthOfMonth = now.lengthOfMonth();
        String monthvalue = String.format("%02d", month.getValue());
        String daykey1=year+"-"+monthvalue+"-";
        String mun="mou"+daykey1;


        for (int day = 1; day <= lengthOfMonth; day++) {
            String dayKey = "deu"+daykey1+ String.format("%02d", day);
            System.out.println(dayKey);
            dayKeys.add(dayKey);
        }
        RedisCallback<Long> redisCallback = new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
               return connection.bitOp(RedisStringCommands.BitOperation.OR,
                        mun.getBytes(),
                        dayKeys.stream().map(String::getBytes).toArray(byte[][]::new)
                        );
            };
        };
        stringRedisTemplate.execute(redisCallback);

       RedisCallback<Long> redisCallback1=new RedisCallback<Long>() {
           @Override
           public Long doInRedis(RedisConnection connection) throws DataAccessException {
              return connection.bitCount(mun.getBytes());
           }
       };

        Long execute = stringRedisTemplate.execute(redisCallback1);
        System.out.println(execute);

    }
    @Test
    void setShopService5() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        System.out.println(year);
        Month month = now.getMonth();
        System.out.println(month.getValue());

    }
}
