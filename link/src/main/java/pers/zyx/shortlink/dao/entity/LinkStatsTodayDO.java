package pers.zyx.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接今日统计实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_stats_today")
public class LinkStatsTodayDO{

    /**
    * ID
    */
    private Long id;

    /**
    * 分组标识
    */
    private String gid;

    /**
    * 短链接
    */
    private String fullShortUrl;

    /**
    * 日期
    */
    private Date date;

    /**
    * 今日PV
    */
    private Integer todayPv;

    /**
    * 今日UV
    */
    private Integer todayUv;

    /**
    * 今日IP数
    */
    private Integer todayUip;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改时间
    */
    private Date updateTime;

    /**
    * 删除标识 0：未删除 1：已删除
    */
    private Integer delFlag;
}