package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import pers.zyx.shortlink.dao.entity.LinkDeviceStatsDO;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {
    @Insert("""
            INSERT INTO t_link_device_stats(gid, full_short_url, date, cnt, device, create_time, update_time, del_flag)
            VALUES( #{linkDeviceStats.gid}, #{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.date}, #{linkDeviceStats.cnt}, #{linkDeviceStats.device}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE 
                cnt = cnt +  #{linkDeviceStats.cnt};
        """)
    void shortLinkDeviceState(@Param("linkDeviceStats") LinkDeviceStatsDO linkDeviceStatsDO);
}
