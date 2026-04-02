package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IUserService;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.StringJoiner;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Resource
    public IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryUserByid(Long id) {
        System.out.println("9999");
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("用户不存在");
        }
        blogSet(blog);
        isBlogIsLike(blog);
        return Result.ok(blog);

    }

    @Override
    public void blogSet(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    @Override
    public Result queryBlogList(Integer current) {
        System.out.println("开始查询");
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            this.blogSet(blog);
            this.isBlogIsLike(blog);

        });
        return Result.ok(records);
    }

    public void isBlogIsLike(Blog blog) {

        Long userid = UserHolder.getUser().getId();
        String key = "islike:blog:" +blog.getId() ;
//        判断用户是否点过赞
        Double islike = stringRedisTemplate.opsForZSet().score(key, userid.toString());
        blog.setIsLike(islike!=null);
    }

    @Override
    public Result queryIsLike(Long id) {
//        获取用户
        Long userid = UserHolder.getUser().getId();
        String key = "islike:blog:" + id;
//        判断用户是否点过赞
        Double islike = stringRedisTemplate.opsForZSet().score(key, userid.toString());

        if (islike==null) {
//            不存在 数据库点赞数加1
            boolean issuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (issuccess) {

//                存储到redis中
                stringRedisTemplate.opsForZSet().add(key, userid.toString(),System.currentTimeMillis());
            }
//存在


        } else {
            boolean success = update().setSql("liked = liked - 1").eq("id", id).update();
            if (success) {
                stringRedisTemplate.opsForZSet().remove(key, userid.toString());

            }
        }
        return Result.ok();
    }


}
