package pers.zyx.shortlink.remote.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.remote.service.LinkRemoteService;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;

@RestController
public class RecycleBinController {
    LinkRemoteService shortLinkRemoteService = new LinkRemoteService() {};

    /**
     * 将链接移至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> SaveRecycleBin(@RequestBody SaveRecycleBinReqDTO requestParam) {
        shortLinkRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }
}