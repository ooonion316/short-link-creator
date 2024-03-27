package pers.zyx.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.service.LinkService;

@RestController
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    /**
     * 创建短链接
     */
    @PostMapping("/api/short-link/v1/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        ShortLinkCreateRespDTO result = linkService.createShortLink(requestParam);
        return Results.success(result);
    }
}
