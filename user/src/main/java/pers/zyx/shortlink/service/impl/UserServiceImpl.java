package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.zyx.shortlink.biz.user.UserContext;
import pers.zyx.shortlink.dao.entity.UserDO;
import pers.zyx.shortlink.mapper.UserMapper;
import pers.zyx.shortlink.dto.req.UserLoginReqDTO;
import pers.zyx.shortlink.dto.req.UserRegisterReqDTO;
import pers.zyx.shortlink.dto.req.UserUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.UserInfoRespDTO;
import pers.zyx.shortlink.dto.resp.UserLoginRespDTO;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.service.GroupService;
import pers.zyx.shortlink.service.UserService;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pers.zyx.shortlink.constant.UserRedisKeyConstant.USER_LOGIN_PREFIX;
import static pers.zyx.shortlink.constant.UserRedisKeyConstant.USER_REGISTER_LOCK;
import static pers.zyx.shortlink.errorcode.UserErrorCodeEnum.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {
    private final StringRedisTemplate stringRedisTemplate;
    private final RBloomFilter<String> userRegisterBloomFilter;
    private final RedissonClient redissonClient;

    private final GroupService groupService;



    @Override
    public Boolean hasUsername(String username) {
        return userRegisterBloomFilter.contains(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
                groupService.saveGroup(requestParam.getUsername(), "默认分组");
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

    @Override
    public void updateUser(UserUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, UserContext.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        Boolean hasUsername = hasUsername(requestParam.getUsername());
        if (!hasUsername) {
            throw new ClientException(USER_NULL);
        }

        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword());
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        if (userDO == null) {
            throw new ClientException(USER_NULL);
        }

        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash().entries(USER_LOGIN_PREFIX + requestParam.getUsername());
        if (!hasLoginMap.isEmpty()) {
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登陆错误"));
            return new UserLoginRespDTO(token);
        }

        String token = UUID.randomUUID().toString(true);
        stringRedisTemplate.opsForHash().put(USER_LOGIN_PREFIX + requestParam.getUsername(), token, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(USER_LOGIN_PREFIX + requestParam.getUsername(), 30L, TimeUnit.MINUTES);

        return new UserLoginRespDTO(token);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(USER_LOGIN_PREFIX + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if (!checkLogin(username, token)) {
            throw new ClientException(USER_NULL);
        }
        stringRedisTemplate.delete(USER_LOGIN_PREFIX + username);
    }
}
