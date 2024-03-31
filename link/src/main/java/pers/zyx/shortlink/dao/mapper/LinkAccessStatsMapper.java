package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.zyx.shortlink.dao.entity.LinkAccessStatsDO;

@Mapper
public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {
    /**
     * 记录基础访问统计数据
     */
    @Insert("""
            INSERT INTO t_link_access_stats(gid, full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag)
            VALUES(#{linkAccessStats.gid}, #{linkAccessStats.fullShortUrl}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour}, #{linkAccessStats.weekday}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE
                update_time = NOW(),
                pv = pv + #{linkAccessStats.pv},
                uv = uv + #{linkAccessStats.uv},
                uip = uip + #{linkAccessStats.uip};
            
        """)
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);

}