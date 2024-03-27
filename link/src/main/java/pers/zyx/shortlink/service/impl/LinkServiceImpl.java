package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dao.mapper.LinkMapper;
import pers.zyx.shortlink.service.LinkService;

@Service
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
}
