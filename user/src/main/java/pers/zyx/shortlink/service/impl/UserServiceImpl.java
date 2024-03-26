package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.UserDO;
import pers.zyx.shortlink.dao.mapper.UserMapper;
import pers.zyx.shortlink.service.UserService;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
}
