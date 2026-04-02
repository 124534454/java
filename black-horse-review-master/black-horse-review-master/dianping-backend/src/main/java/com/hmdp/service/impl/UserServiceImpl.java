package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RegexPatterns;
import com.hmdp.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.Token;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
//    模拟发送验证码
    public Result sendCode(String phone, HttpSession session) {

//1.校验手机号
//        if (!RegexUtils.isPhoneInvalid(phone)) {
//            //        不符合报错
//            return Result.fail("手机号不合规");
//        }
////        符合生成验证码
//        String code = RandomUtil.randomNumbers(6);
////        保存验证码到session
//        session.setAttribute("code",code);
////        发送验证码
//        log.debug("验证码发送成功",code);

//        验证手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
//        生成验证码
        String code = RandomUtil.randomNumbers(6);

//        存储到session中
//        session.setAttribute("code",code);
//        session.setAttribute("code",code);
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        log.debug("验证码发送成功" + code);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {


//        提交手机号和验证码
//        String phone1 = loginForm.getPhone();
//        String usercode=loginForm.getCode();
//        Object sessioncode = session.getAttribute("code");
//        if(sessioncode==null||!usercode.equals(sessioncode.toString())){
//            return Result.fail("验证码错误");
//        }
////        根据手机号查询用户
//        User user2 = query().eq("phone", phone1).one();
//        if(user2==null){
////            创建用户
//            User user = new User();
//            user.setPhone(phone1);
//            user.setNickName("user_"+RandomUtil.randomString(10));
//            save(user);
//            session.setAttribute("user",user2);
//            return Result.ok();
//        }
//        //            保存用户到session中
//        session.setAttribute("user",user2);


        User user = null;
//        验证手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号格式错误");
        }
//        获取验证码
        String code = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        String loginFormCode = loginForm.getCode();
//        String sessionCode = (String) session.getAttribute("code");
        if (loginFormCode == null || !code.equals(loginFormCode)) {
            return Result.fail("验证码错误");
        } else {
//            根据手机号查询用户
            user = query().eq("phone", phone).one();
            if (user == null) {
//                创建用户
                user = createUser(phone);
//                将用户加入数据库中
                save(user);

            }
//            将用户存入session
            String TOKEN = UUID.randomUUID().toString(true);
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
//            session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
            Map<String, Object> stringObjectMap = BeanUtil.beanToMap
                    (userDTO, new HashMap<>(), CopyOptions.create()
                            .setIgnoreNullValue(true).setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())
                    );
            stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + TOKEN, stringObjectMap);
//            设置token有效期
            stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + TOKEN, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
            return Result.ok(TOKEN);
        }

    }

    private User createUser(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_" + RandomUtil.randomString(10));
        return user;
    }
}
