package pers.zyx.shortlink.remote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.req.ShortLinkGroupStatsReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkStatsRespDTO;
import pers.zyx.shortlink.remote.service.LinkRemoteService;
import pers.zyx.shortlink.result.Result;

@RestController
@RequiredArgsConstructor
public class ShortLinkStatsController {
    LinkRemoteService shortLinkRemoteService = new LinkRemoteService() {};


    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkRemoteService.groupShortLinkStats(requestParam);
    }
}
