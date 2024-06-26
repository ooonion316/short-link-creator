package pers.zyx.shortlink.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pers.zyx.shortlink.remote.req.*;
import pers.zyx.shortlink.remote.resp.*;
import pers.zyx.shortlink.result.Result;

import java.util.List;
import java.util.Map;

@FeignClient(value = "short-link-creator-link", url = "${aggregation.remote-url:}")
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

    /**
     * 根据 URL 获取网站标题
     *
     * @param url 网站地址
     * @return 网站标题
     */
    @GetMapping("/api/short-link/v1/title")
    Result<String> getTitleByUrl(@RequestParam("url") String url);

    /**
     * 将链接移至回收站
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/save")
    void saveRecycleBin(@RequestBody SaveRecycleBinReqDTO requestParam);

    /**
     * 将链接移出回收站
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/recover")
    void recoverRecycleBin(@RequestBody RecoverRecycleBinReqDTO requestParam);

    /**
     * 将链接从回收站中删除
     *
     * @param requestParam 请求参数
     */
    @PostMapping("/api/short-link/v1/recycle-bin/delete")
    void deleteRecycleBin(@RequestBody DeleteRecycleBinReqDTO requestParam);

    /**
     * 查询回收站内短链接
     *
     * @param gidList   分组标识
     * @param current   当前页码
     * @param size      当前页数据量
     * @return          返回响应
     */
    @GetMapping("/api/short-link/v1/recycle-bin/page")
    Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(@RequestParam("gidList") List<String> gidList,
                                                               @RequestParam("current") Long current,
                                                               @RequestParam("size") Long size);

    /**
     * 访问单个短链接指定时间内监控数据
     *
     * @param requestParam 请求参数
     * @return 返回结果
     */
    @GetMapping("/api/short-link/v1/stats")
    Result<ShortLinkStatsRespDTO> shortLinkStats(@SpringQueryMap ShortLinkStatsReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内监控数据
     *
     * @param requestParam 请求参数
     * @return 返回响应
     */
    @GetMapping("/api/short-link/v1/stats/group")
    Result<ShortLinkStatsRespDTO> groupShortLinkStats(@SpringQueryMap ShortLinkGroupStatsReqDTO requestParam);


    /**
     * 访问单个短链接指定时间内监控访问记录数据
     */
    @GetMapping("/api/short-link/v1/stats/access-record")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(@SpringQueryMap ShortLinkStatsAccessRecordReqDTO requestParam);

    /**
     * 访问分组短链接指定时间内监控访问记录数据
     *
     * @param requestParam 访问分组短链接监控访问记录请求参数
     * @return 分组短链接监控访问记录信息
     */
    @GetMapping("/api/short-link/v1/stats/access-record/group")
    Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(@SpringQueryMap ShortLinkGroupStatsAccessRecordReqDTO requestParam);
}
