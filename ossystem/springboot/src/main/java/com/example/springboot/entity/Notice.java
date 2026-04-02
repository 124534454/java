package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
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
@Schema(name = "Notice对象", description = "")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "标题")
    @Alias("标题")
    private String name;

    @Schema(description = "内容")
    @Alias("内容")
    private String content;

    @Schema(description = "封面")
    @Alias("封面")
    private String img;

    @Schema(description = "1-简单的公告，2富文本")
    @Alias("1-简单的公告，2富文本")
    private Integer type;


}
