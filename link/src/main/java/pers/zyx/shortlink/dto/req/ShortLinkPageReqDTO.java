package pers.zyx.shortlink.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import pers.zyx.shortlink.dao.entity.LinkDO;

/**
 * 分页短链接请求参数
 */
@Data
public class ShortLinkPageReqDTO extends Page<LinkDO> {
    /**
     * 分组标识
     */
    private String gid;
}