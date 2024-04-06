package pers.zyx.shortlink.remote.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.req.ShortLinkBatchCreateReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkBaseInfoRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkBatchCreateRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.remote.service.LinkRemoteService;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.util.EasyExcelWebUtil;

import java.util.List;

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

    /**
     * 中台调用批量创建短链接
     */
    @SneakyThrows
    @PostMapping("/api/short-link/admin/v1/create/batch")
    public void batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam, HttpServletResponse response) {
        Result<ShortLinkBatchCreateRespDTO> shortLinkBatchCreateRespDTOResult = linkRemoteService.batchCreateShortLink(requestParam);
        if (shortLinkBatchCreateRespDTOResult.isSuccess()) {
            List<ShortLinkBaseInfoRespDTO> baseLinkInfos = shortLinkBatchCreateRespDTOResult.getData().getBaseLinkInfos();
            EasyExcelWebUtil.write(response, "批量创建短链接-SaaS短链接系统", ShortLinkBaseInfoRespDTO.class, baseLinkInfos);
        }
    }

    /**
     * 分页查询短链接
     */
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return linkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 更新短链接
     */
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        linkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }
}
