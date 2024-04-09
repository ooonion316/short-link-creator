package pers.zyx.shortlink.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pers.zyx.shortlink.remote.req.ShortLinkBatchCreateReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.remote.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkBatchCreateRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkGroupCountRespDTO;
import pers.zyx.shortlink.remote.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.result.Result;

import java.util.List;

@FeignClient("short-link-creator-link")
public interface LinkActualRemoteService {

    /**
     * 创建短链接
     *
     * @param requestParam 请求参数
     * @return 返回响应
     */
    @PostMapping("/api/short-link/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam);

    /**
     * 批量创建短链接
     *
     * @param requestParam 请求参数
     * @return 返回响应
     */
    @PostMapping("/api/short-link/v1/create/batch")
    Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(@RequestBody ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 更新短链接
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/update")
    void updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param gid       分组标识
     * @param orderTag  排序类型
     * @param current   当前页码
     * @param size      当前页数据量
     * @return  返回响应
     */
    @GetMapping("/api/short-link/v1/page")
    Result<Page<ShortLinkPageRespDTO>> pageShortLink(@RequestParam("gid") String gid,
                                                     @RequestParam("orderTag") String orderTag,
                                                     @RequestParam("current") Long current,
                                                     @RequestParam("size") Long size);

    /**
     * 查询分组短链接总量
     *
     * @param gids 分组标识
     * @return 返回响应
     */
    @GetMapping("/api/short-link/v1/count")
    Result<List<ShortLinkGroupCountRespDTO>> countGroupShortLink(@RequestParam("gids") List<String> gids);
}
