package pers.zyx.shortlink.remote.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.LinkActualRemoteService;
import pers.zyx.shortlink.remote.req.ShortLinkGroupStatsAccessRecordReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkGroupStatsReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkStatsReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkStatsAccessRecordRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkStatsRespDTO;
import pers.zyx.shortlink.result.Result;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    private final LinkActualRemoteService linkActualRemoteService;

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return linkActualRemoteService.shortLinkStats(requestParam);
    }


    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return linkActualRemoteService.groupShortLinkStats(requestParam);
    }

    /**
     * 访问单个短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return linkActualRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 访问分组短链接指定时间内访问记录监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return linkActualRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }
}
