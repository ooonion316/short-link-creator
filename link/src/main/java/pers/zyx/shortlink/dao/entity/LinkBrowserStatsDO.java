package pers.zyx.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_browser_stats")
public class LinkBrowserStatsDO {
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
    * 日期
    */
    private Date date;

    /**
    * 访问量
    */
    private Integer cnt;

    /**
    * 浏览器
    */
    private String browser;

    /**
    * 创建时间
    */
    private LocalDateTime createTime;

    /**
    * 修改时间
    */
    private LocalDateTime updateTime;

    /**
    * 删除标识：0 未删除 1 已删除
    */
    private int delFlag;
}