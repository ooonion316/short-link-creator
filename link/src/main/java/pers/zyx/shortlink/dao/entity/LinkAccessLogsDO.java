package pers.zyx.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_access_logs")
public class LinkAccessLogsDO {
    /**
    * id
    */
    private Long id;

    /**
    * 完整短链接
    */
    private String fullShortUrl;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 用户信息
    */
    private String user;

    /**
    * 浏览器
    */
    private String browser;

    /**
    * 操作系统
    */
    private String os;

    /**
    * ip
    */
    private String ip;

    /**
    * 创建时间
    */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
    * 修改时间
    */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
    * 删除标识 0：未删除 1：已删除
    */
    private int delFlag;
}