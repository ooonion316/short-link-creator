package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.zyx.shortlink.dao.entity.LinkDeviceStatsDO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.List;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {
    @Insert("""
            INSERT INTO t_link_device_stats(gid, full_short_url, date, cnt, device, create_time, update_time, del_flag)
            VALUES( #{linkDeviceStats.gid}, #{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.date}, #{linkDeviceStats.cnt}, #{linkDeviceStats.device}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE 
                cnt = cnt +  #{linkDeviceStats.cnt};
        """)
    void shortLinkDeviceState(@Param("linkDeviceStats") LinkDeviceStatsDO linkDeviceStatsDO);

    /**
     * 根据短链接获取指定日期内访问设备统计数据
     */
    @Select("""
            SELECT
                device,
                SUM(cnt) AS cnt
            FROM
                t_link_device_stats
            WHERE
                full_short_url = #{param.fullShortUrl}
                AND gid = #{param.gid}
                AND date BETWEEN #{param.startDate} AND #{param.endDate}
            GROUP BY
                full_short_url,
                gid,
                device
        """)
    List<LinkDeviceStatsDO> listDevicesStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
}
