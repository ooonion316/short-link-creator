package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkLocaleStatsDO;
import pers.zyx.shortlink.dao.mapper.LinkLocaleStatsMapper;
import pers.zyx.shortlink.service.LinkLocaleStatsService;

@Service
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStatsDO> implements LinkLocaleStatsService {
}