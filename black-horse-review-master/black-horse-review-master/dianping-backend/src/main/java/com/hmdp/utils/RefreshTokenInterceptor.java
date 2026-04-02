package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor {
    @Resource
    private  StringRedisTemplate stringRedisTemplate;
   public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate){
       this.stringRedisTemplate=stringRedisTemplate;
   }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        获取请求头中的token
            String token = request.getHeader("authorization");
            if(StrUtil.isBlank(token)){

                return true;
            }
//        基于token获取用户
            Map<Object, Object> usermap = stringRedisTemplate.opsForHash().entries(RedisConstants.LOGIN_USER_KEY + token);
            if(usermap.isEmpty()){

                return  true;
            }
            UserDTO userDTO = BeanUtil.fillBeanWithMap(usermap, new UserDTO(), false);
            UserHolder.saveUser(userDTO);
//        刷新token有效期
            stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token,RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
            return  true;    }


}
