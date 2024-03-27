package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.GroupDO;
import pers.zyx.shortlink.dao.mapper.GroupMapper;
import pers.zyx.shortlink.service.GroupService;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
}
