package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.UserDO;
import pers.zyx.shortlink.dao.mapper.UserMapper;
import pers.zyx.shortlink.dto.req.UserRegisterReqDTO;
import pers.zyx.shortlink.dto.resp.UserInfoRespDTO;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.service.UserService;

import static pers.zyx.shortlink.constant.UserRedisCacheConstant.USER_REGISTER_LOCK;
import static pers.zyx.shortlink.errorcode.UserErrorCodeEnum.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final RBloomFilter<String> userRegisterBloomFilter;
    private final RedissonClient redissonClient;

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_EXIST);
        }
        RLock lock = redissonClient.getLock(USER_REGISTER_LOCK + requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int insert = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if (insert < 1) {
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterBloomFilter.add(requestParam.getUsername());
            } else {
                throw new ClientException(USER_EXIST);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public UserInfoRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(USER_NULL);
        }
        return BeanUtil.copyProperties(userDO, UserInfoRespDTO.class);
    }
}
