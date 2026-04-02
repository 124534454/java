package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisData;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    public StringRedisTemplate stringRedisTemplate;
    String key = "cache:shop:";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @PostConstruct
    public void init() {
        for (Long i = 1L; i <= 14; i++) {
            saveShop1Redis(i,20L);

        }
    }
    @Override
    public Result getByIdShop(Long id) {
        Shop shop = queryWithLogicalExpireTime(id);
        if (shop == null) {
            return Result.fail("店铺不存在");
        }
        return Result.ok(shop);

    }

//    public Shop queryWithPassThrough(Long id) {
////        根据id从redis查数据
//        String shopstr = stringRedisTemplate.opsForValue().get(key + id);
////        判断是否为空
//        if (StrUtil.isNotBlank(shopstr)) {
//            Shop shop = JSONUtil.toBean(shopstr, Shop.class);
//            return shop;
//        }
//        if (shopstr != null) {
//            return null;
//        }
//        Shop shop1 = null;
//
//        String lock = null;
//        try {
//            lock = "CACHE:shop:lock";
////    获取锁
//            boolean lockflag = tryLock(lock + id);
//            if (!lockflag) {
//                //        没有获取成功
//                Thread.sleep(10);
//                return queryWithPassThrough(id);
//            }
//
//
//            //不存在查数据库
//            shop1 = getById(id);
//            if (shop1 == null) {
//                //   解决缓存穿透
//                stringRedisTemplate.opsForValue().set(key + id, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
//
//                return null;
//            }
//            //将数据保存到redis中
//            stringRedisTemplate.opsForValue().set(key + id, JSONUtil.toJsonStr(shop1), RedisConstants.LOCK_SHOP_TTL, TimeUnit.MINUTES);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            delLock(lock + id);
//
//        }
//        return shop1;
//
//
//    }

    public Shop queryWithLogicalExpireTime(Long id) {
//        根据id从redis查数据
        String shopstr = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);
//        判断是否为空
        if (StrUtil.isBlank(shopstr)) {
            return null;
        }
//        不为空
        RedisData redisData = JSONUtil.toBean(shopstr, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();

//      判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
//            没过期
            return shop;
        }
//        过期缓存重建
//获取互斥锁
        String lockkey = RedisConstants.LOCK_SHOP_KEY + id;

        boolean tryLock = tryLock(lockkey);
        if (tryLock) {
//            获取成功开启独立线程重建缓存
            executorService.submit(() -> {
                try {
                    this.saveShop1Redis(id,20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    delLock(lockkey);
                }
            });
        }
        return shop;


    }

    //更新缓存
    public void saveShop1Redis(Long id, Long localDateTime) {
//        查询商铺信息
        Shop shop = getById(id);
        RedisData redisData = new RedisData();

        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(localDateTime));
//    存入redis
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }


    //获取锁
    public boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);

    }

    //    释放锁
    public boolean delLock(String key) {
        return BooleanUtil.isTrue(stringRedisTemplate.delete(key));

    }

    @Override
    @Transactional
    public Result updateShop(Shop shop) {
        System.out.println(shop.getId());
        if (shop.getId() == null) {
            return Result.fail("商铺id不能为空");
        }
//        更新数据库
        updateById(shop);
//        清楚redis中的
        stringRedisTemplate.delete(key + shop.getId());
        return Result.ok();
    }
}
