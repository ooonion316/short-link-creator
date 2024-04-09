package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dto.req.DeleteRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.RecoverRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkRecycleBinPageReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;

public interface RecycleBinService extends IService<LinkDO> {
    /**
     * 将短链接移至回收站
     *
     * @param requestParam 链接请求参数
     */
    void saveRecycleBin(SaveRecycleBinReqDTO requestParam);

    /**
     * 将短链接移出回收站
     *
     * @param requestParam 链接请求参数
     */
    void recoverRecycleBin(RecoverRecycleBinReqDTO requestParam);

    /**
     * 将链接从回收站中删除
     *
     * @param requestParam 链接请求参数
     */
    void deleteRecycleBin(DeleteRecycleBinReqDTO requestParam);

    /**
     * 分页查询回收站内短链接
     *
     * @param requestParam 分页查询请求参数
     * @return 分页查询返回结果
     */
    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam);
}