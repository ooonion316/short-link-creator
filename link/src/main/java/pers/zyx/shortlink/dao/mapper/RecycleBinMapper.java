package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dto.req.ShortLinkRecycleBinPageReqDTO;

@Mapper
public interface RecycleBinMapper extends BaseMapper<LinkDO> {
    /**
     * 分页查询回收站内短链接
     */
    IPage<LinkDO> pageRecycleBinLink(ShortLinkRecycleBinPageReqDTO requestParam);
}