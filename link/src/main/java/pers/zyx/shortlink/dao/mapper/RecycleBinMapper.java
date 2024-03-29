package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.zyx.shortlink.dao.entity.LinkDO;

@Mapper
public interface RecycleBinMapper extends BaseMapper<LinkDO> {
}