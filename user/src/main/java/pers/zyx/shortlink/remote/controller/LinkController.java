package pers.zyx.shortlink.remote.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.remote.service.LinkRemoteService;
import pers.zyx.shortlink.result.Result;

/**
 * 链接中台调用, 后期使用 SpringCloud 代替
 */
@RestController
public class LinkController {
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };

    /**
     * 中台调用创建短链接
     */
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return linkRemoteService.createShortLink(requestParam);
    }
}
