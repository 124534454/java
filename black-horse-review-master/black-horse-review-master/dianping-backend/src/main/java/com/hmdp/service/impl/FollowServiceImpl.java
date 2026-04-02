package com.hmdp.service.impl;

import com.hmdp.entity.Follow;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {
//判断用户是否关注 没关注则关注关注则取关
    @Override
    public void followOrNotUser(Long id, boolean isfollow) {

    }

    @Override
    public void userIsOrNOFollow(Long id) {

    }
}
