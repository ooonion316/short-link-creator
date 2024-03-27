package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;

public interface LinkService extends IService<LinkDO> {
    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接参数
     * @return 返回实体
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
