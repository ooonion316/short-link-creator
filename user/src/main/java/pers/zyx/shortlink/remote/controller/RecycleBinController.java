package pers.zyx.shortlink.remote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.LinkActualRemoteService;
import pers.zyx.shortlink.remote.req.DeleteRecycleBinReqDTO;
import pers.zyx.shortlink.remote.req.RecoverRecycleBinReqDTO;
import pers.zyx.shortlink.remote.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.remote.service.LinkRemoteService;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;

@RestController
@RequiredArgsConstructor
public class RecycleBinController {
    private final LinkActualRemoteService linkActualRemoteService;

    /**
     * 将链接移至回收站
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/save")
    public Result<Void> SaveRecycleBin(@RequestBody SaveRecycleBinReqDTO requestParam) {
        linkActualRemoteService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 将链接从回收站中恢复
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecoverRecycleBinReqDTO requestParam) {
        linkActualRemoteService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 将链接从回收站中删除
     */
    @PostMapping("/api/short-link/admin/v1/recycle-bin/delete")
    public Result<Void> deleteRecycleBin(@RequestBody DeleteRecycleBinReqDTO requestParam) {
        linkActualRemoteService.deleteRecycleBin(requestParam);
        return Results.success();
    }
}