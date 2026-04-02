package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import cn.hutool.core.annotation.Alias;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-03-19
 */
@Getter
@Setter
@Schema(name = "Youjian对象", description = "")
public class Youjian implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "发送邮件账号")
    @Alias("发送邮件账号")
    private String sendemail;

    @Schema(description = "接收邮件账号")
    @Alias("接收邮件账号")
    private String receiveemail;

    @Schema(description = "邮件内容")
    @Alias("邮件内容")
    private String content;

    @Schema(description = "发送时间")
    @Alias("发送时间")
    private String createtime;

    @Schema(description = "发邮件的用户id")
    @Alias("发邮件的用户id")
    private Integer userid;

    @Schema(description = "邮件标题")
    @Alias("邮件标题")
    private String title;


}
