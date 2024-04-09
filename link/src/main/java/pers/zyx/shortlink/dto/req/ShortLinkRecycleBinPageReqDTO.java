package pers.zyx.shortlink.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import pers.zyx.shortlink.dao.entity.LinkDO;

import java.util.List;

/**
 * 查看回收站短链接分页请求参数
 */
@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<LinkDO> {

    /**
     * 分组标识
     */
    private List<String> gidList;
}