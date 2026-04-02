package com.hmdp.controller;


import com.hmdp.service.IFollowService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private IFollowService followService;
    @PutMapping("/{id}/{isfollow}")
    public void follow(@PathVariable Long  id, @PathVariable boolean isfollow) {
        followService.followOrNotUser(id,isfollow);
    }
    @GetMapping("/or/not/{id}")
    public void isfollow(@PathVariable Long id) {
        followService.userIsOrNOFollow(id);

    }

}
