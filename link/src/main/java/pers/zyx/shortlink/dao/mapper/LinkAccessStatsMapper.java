package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import pers.zyx.shortlink.dao.entity.LinkAccessStatsDO;
import pers.zyx.shortlink.dto.req.ShortLinkGroupStatsReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

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

    /**
     * 根据短链接获取指定日期内基础统计数据
     */
    @Select("""
            SELECT
                date,
                SUM(pv) AS pv,
                SUM(uv) AS uv,
                SUM(uip) AS uip
            FROM t_link_access_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                date
        """)
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param")ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内每小时基础统计数据
     */
    @Select("""
            SELECT
                hour,
                SUM(pv) AS pv
            FROM t_link_access_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                hour
        """)
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param")ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内每周基础统计数据
     */
    @Select("""
            SELECT
                weekday,
                SUM(pv) AS pv
            FROM t_link_access_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                weekday
        """)
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param")ShortLinkStatsReqDTO requestParam);


    /**
     * 根据分组获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    date, " +
            "    SUM(pv) AS pv, " +
            "    SUM(uv) AS uv, " +
            "    SUM(uip) AS uip " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, date;")
    List<LinkAccessStatsDO> listStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    hour, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, hour;")
    List<LinkAccessStatsDO> listHourStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内每周基础监控数据
     */
    @Select("SELECT " +
            "    weekday, " +
            "    SUM(pv) AS pv " +
            "FROM " +
            "    t_link_access_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    @Select("""
            SELECT
                full_short_url,
                SUM(CASE WHEN date = CURDATE() THEN pv ELSE 0 END) AS today_pv,
                SUM(CASE WHEN date = CURDATE() THEN uv ELSE 0 END) AS today_uv,
                SUM(CASE WHEN date = CURDATE() THEN uip ELSE 0 END) AS today_uip,
                SUM(pv) AS total_pv,
                SUM(uv) AS total_uv,
                SUM(uip) AS total_uip
            FROM t_link_access_stats
            WHERE gid = #{gid}
            group by full_short_url
        """)
   List<ShortLinkPageRespDTO> listLinkStatsByGroup(@Param("gid") String gid);
}