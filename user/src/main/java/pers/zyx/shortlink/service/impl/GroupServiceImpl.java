package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.biz.user.UserContext;
import pers.zyx.shortlink.dao.BaseDO;
import pers.zyx.shortlink.dao.entity.GroupDO;
import pers.zyx.shortlink.dao.mapper.GroupMapper;
import pers.zyx.shortlink.dto.req.GroupSortReqDTO;
import pers.zyx.shortlink.dto.req.GroupUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.GroupListRespDTO;
import pers.zyx.shortlink.service.GroupService;

import java.util.List;
import java.util.Optional;

import static pers.zyx.shortlink.constant.UserRedisKeyConstant.GROUP_CREATE_LOCK;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    private final RedissonClient redissonClient;

    @Override
    public void saveGroup(String groupName) {
        this.saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        RLock lock = redissonClient.getLock(String.format(GROUP_CREATE_LOCK, username));
        lock.lock();
        try {
            String gid = RandomUtil.randomString(6);
            while(hasGid(username, gid)) gid = RandomUtil.randomString(6);
            GroupDO groupDO = GroupDO.builder()
                    .gid(gid)
                    .sortOrder(0)
                    .name(groupName)
                    .username(username)
                    .build();
            baseMapper.insert(groupDO);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<GroupListRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(lambdaQueryWrapper);
        return BeanUtil.copyToList(groupDOList, GroupListRespDTO.class);
    }

    @Override
    public void updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getName, requestParam.getName());
        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .build();
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void removeGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .set(BaseDO::getDelFlag, 1);
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        baseMapper.update(groupDO, updateWrapper);
    }

    @Override
    public void sortGroup(List<GroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaQueryWrapper<GroupDO> lambdaQueryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid());
            baseMapper.update(groupDO, lambdaQueryWrapper);
    });
    }

    public boolean hasGid(String username, String gid) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, Optional.ofNullable(username).orElse(UserContext.getUsername()));
        GroupDO groupDO = baseMapper.selectOne(queryWrapper);
        return groupDO != null;
    }
}
