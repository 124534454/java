package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisIdWorker redisIdWorker;
@Resource
private RedissonClient redissonClient;
    @Override
    public Result seckillVoucher(Long voucherOrder) {
//        查询优惠卷信息
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherOrder);
//        是否未开始
        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
            return Result.fail("秒杀未开始");
        }
//        判断是否结束
        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
            return Result.fail("活动已经结束");
        }
//        判断库存是否充足
        if (seckillVoucher.getStock() < 1) {
            return Result.fail("库存不足");
        }
        Long userid = UserHolder.getUser().getId();
//        SimpleRedisLock simpleRedisLock = new SimpleRedisLock("order:" + userid, stringRedisTemplate);
        RLock lock = redissonClient.getLock("order" + userid);
        boolean tryLock = lock.tryLock();
//        boolean success = simpleRedisLock.tryLock(1200L);
        if (!tryLock) {
            return Result.fail("不能重复下单");
        }
        try {
            IVoucherOrderService currentProxy = (IVoucherOrderService) AopContext.currentProxy();
            return currentProxy.createVoucherOrder(voucherOrder);


        } finally {
            lock.unlock();
        }
//        synchronized (userid.toString().intern()){
//            IVoucherOrderService currentProxy =(IVoucherOrderService) AopContext.currentProxy();
//            return currentProxy.createVoucherOrder(voucherOrder);
//
//        }

    }

    @Transactional
    public Result createVoucherOrder(Long voucherOrder) {
        Long userid = UserHolder.getUser().getId();

        //        实现一人一单功能
//        根据订单id和用户id查询数据库
        Long counted = query().eq("voucher_id", voucherOrder).eq("user_id", userid).count();

        if (counted > 0) {
            return Result.fail("不能重复下单");
        }
//        更新库存
        boolean success =
                seckillVoucherService.update().setSql("stock=stock-1").eq("voucher_id", voucherOrder)
                        .gt("stock", 0).update();
        if (!success) {
            return Result.fail("库存不足");

        }
//        生成订单
        VoucherOrder voucherOrder1 = new VoucherOrder();
        voucherOrder1.setVoucherId(voucherOrder);

        voucherOrder1.setUserId(userid);
//        订单id
        long order = redisIdWorker.nextId("order");
        voucherOrder1.setId(order);
        save(voucherOrder1);
        return Result.ok(order);
    }


}
