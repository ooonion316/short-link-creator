package pers.zyx.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkGroupCountRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.service.LinkService;

import java.util.List;

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


    /**
     * 查询指定分组下的所有短链接
     */
    @GetMapping("/api/short-link/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkPageRespDTO> result = linkService.pageShortLink(requestParam);
        return Results.success(result);
    }

    /**
     * 统计各个分组的短链接数量
     */
    @GetMapping("/api/short-link/v1/count")
    public Result<List<ShortLinkGroupCountRespDTO>> countGroupShortLink(@RequestParam("gids") List<String> gids) {
        return Results.success(linkService.countGroupShortLink(gids));
    }

    /**
     * 更新短链接
     */
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        linkService.updateShortLink(requestParam);
        return Results.success();
    }
}
