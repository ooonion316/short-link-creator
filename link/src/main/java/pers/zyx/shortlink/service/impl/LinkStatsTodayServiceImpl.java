package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkStatsTodayDO;
import pers.zyx.shortlink.dao.mapper.LinkStatsTodayMapper;
import pers.zyx.shortlink.service.LinkStatsTodayService;

@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsTodayDO> implements LinkStatsTodayService {
}