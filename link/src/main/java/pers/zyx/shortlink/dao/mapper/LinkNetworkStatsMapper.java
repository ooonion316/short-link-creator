package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.zyx.shortlink.dao.entity.LinkNetworkStatsDO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {
    /**
     * 记录访问设备监控数据
     */
    @Insert("""
            INSERT INTO t_link_network_stats(gid, full_short_url, date, cnt, network, create_time, update_time, del_flag)
            VALUES(#{linkNetworkStats.gid}, #{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.date}, #{linkNetworkStats.cnt}, #{linkNetworkStats.network}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE
                cnt = cnt +  #{linkNetworkStats.cnt};
        """)
    void shortLinkNetworkState(@Param("linkNetworkStats") LinkNetworkStatsDO linkNetworkStatsDO);

    /**
     * 根据短链接获取指定日期内访问网络统计数据
     */
    @Select("""
            SELECT
                network,
                SUM(cnt) AS cnt
            FROM
                t_link_network_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                network
        """)
    List<LinkNetworkStatsDO> listNetworkStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}

