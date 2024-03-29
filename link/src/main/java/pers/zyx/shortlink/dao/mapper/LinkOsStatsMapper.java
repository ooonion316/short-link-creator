package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.zyx.shortlink.dao.entity.LinkOsStatsDO;

@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {
    /**
     * 记录操作系统访问统计数据
     */
    @Insert("""
            INSERT INTO t_link_os_stats (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag)
             VALUES(#{linkOsStats.fullShortUrl}, #{linkOsStats.gid}, #{linkOsStats.date}, #{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0)
             ON DUPLICATE KEY UPDATE 
                cnt = cnt + #{linkOsStats.cnt};
        """)
    void shortLinkOsState(@Param("linkOsStats") LinkOsStatsDO linkOsStatsDO);
}
