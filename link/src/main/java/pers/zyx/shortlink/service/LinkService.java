package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dto.biz.ShortLinkStatsRecordDTO;
import pers.zyx.shortlink.dto.req.ShortLinkBatchCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkBatchCreateRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkGroupCountRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

public interface LinkService extends IService<LinkDO> {
    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接参数
     * @return 返回实体
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);


    /**
     * 查询指定分组下的所有短链接
     *
     * @param requestParam 查询参数
     * @return 指定分组下所有短链接
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    /**
     * 统计各个分组的短链接数量
     *
     * @param gids 分组id
     * @return 各个分组短链接数量
     */
    List<ShortLinkGroupCountRespDTO> countGroupShortLink(List<String> gids);

    /**
     * 更新短链接
     *
     * @param requestParam 更新参数
     * @return 更新后信息
     */
    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    /**
     * 短链接跳转
     *
     * @param shortUri  短链接后缀
     * @param request   请求
     * @param response  响应
     */
    void restoreUri(String shortUri, HttpServletRequest request, HttpServletResponse response);

    /**
     * 批量创建短链接
     *
     * @param requestParam 批量创建短链接请求参数
     * @return 批量创建短链接返回参数
     */
    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam);

    /**
     * 短链接统计
     *
     * @param fullShortUrl         完整短链接
     * @param gid                  分组标识
     * @param shortLinkStatsRecord 短链接统计实体参数
     */
    void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO shortLinkStatsRecord);
}
