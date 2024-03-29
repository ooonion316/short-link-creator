package pers.zyx.shortlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dao.mapper.RecycleBinMapper;
import pers.zyx.shortlink.dto.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.service.RecycleBinService;

import static pers.zyx.shortlink.constant.LinkGotoConstant.GOTO_SHORT_LINK;


@Service
@RequiredArgsConstructor
public class RecycleBinServiceImpl extends ServiceImpl<RecycleBinMapper, LinkDO> implements RecycleBinService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveRecycleBin(SaveRecycleBinReqDTO requestParam) {
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getEnableStatus, 0)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUri());
        LinkDO linkDO = LinkDO
                .builder()
                .enableStatus(1)
                .build();
        baseMapper.update(linkDO, updateWrapper);

        stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK, requestParam.getFullShortUri()));
    }
}