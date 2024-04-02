package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.zyx.shortlink.dao.entity.LinkOsStatsDO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;

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

    /**
     * 根据短链接获取指定日期内操作系统访问统计数据
     */
    @Select("""
            SELECT
                os,
                SUM(cnt) AS count
            FROM
                t_link_os_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                os
        """)
    List<HashMap<String, Object>> listOsStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

}
