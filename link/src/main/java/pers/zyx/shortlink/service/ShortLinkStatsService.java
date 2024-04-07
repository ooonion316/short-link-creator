package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkGroupStatsReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsAccessRecordReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkStatsGroupRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkStatsRespDTO;

public interface ShortLinkStatsService {

    /**
     * 查看单个短链接指定时间内统计数据
     *
     * @param requestParam 请求参数
     * @return 返回结果
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);


    /**
     * 查看分组短链接指定时间内统计数据
     *
     * @param requestParam 请求参数
     * @return 返回结果
     */
    ShortLinkStatsGroupRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 查看单个短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 返回结果
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 查看分组内所有短链接指定时间内访问记录监控数据
     *
     * @param requestParam 请求参数
     * @return 返回结果
     */
    IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkGroupStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
