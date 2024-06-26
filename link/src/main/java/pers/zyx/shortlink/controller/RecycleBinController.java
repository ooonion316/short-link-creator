package pers.zyx.shortlink.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.zyx.shortlink.dto.req.DeleteRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.RecoverRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkRecycleBinPageReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.service.RecycleBinService;

@RestController
@RequestMapping("/api/short-link/v1/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {
    private final RecycleBinService recycleBinService;

    /**
     * 将短链接移至回收站
     */
    @PostMapping("/save")
    public Result<Void> SaveRecycleBin(@RequestBody SaveRecycleBinReqDTO requestParam) {
        recycleBinService.saveRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 将短链接移出回收站
     */
    @PostMapping("/recover")
    public Result<Void> recoverRecycleBin(@RequestBody RecoverRecycleBinReqDTO requestParam) {
        recycleBinService.recoverRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 将链接从回收站中删除
     */
    @PostMapping("/delete")
    public Result<Void> deleteRecycleBin(@RequestBody DeleteRecycleBinReqDTO requestParam) {
        recycleBinService.deleteRecycleBin(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站内短链接
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        return Results.success(recycleBinService.pageShortLink(requestParam));
    }
}
