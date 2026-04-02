package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisConstants;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getShopList() {
        String shopType = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_KEY_SHOP_TYPE);
        if(StrUtil.isNotBlank(shopType)){
            List<ShopType> shopTypeslist = JSONUtil.toList(shopType, ShopType.class);
            return Result.ok(shopTypeslist);
        }
//        查数据库
    List<ShopType> typeList =query().orderByAsc("sort").list();
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_KEY_SHOP_TYPE, JSONUtil.toJsonStr(typeList),RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok(typeList);
    }
}
