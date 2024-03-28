package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkGotoDO;
import pers.zyx.shortlink.dao.mapper.LinkGotoMapper;
import pers.zyx.shortlink.service.LinkGotoService;

@Service
public class LinkGotoServiceImpl extends ServiceImpl<LinkGotoMapper, LinkGotoDO> implements LinkGotoService {
}
