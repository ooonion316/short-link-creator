package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dao.mapper.RecycleBinMapper;
import pers.zyx.shortlink.dto.req.DeleteRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.RecoverRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.SaveRecycleBinReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkRecycleBinPageReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.service.RecycleBinService;

import static pers.zyx.shortlink.constant.LinkGotoRedisKeyConstant.GOTO_IS_NULL_SHORT_LINK;
import static pers.zyx.shortlink.constant.LinkGotoRedisKeyConstant.GOTO_SHORT_LINK;


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

    @Override
    public void recoverRecycleBin(RecoverRecycleBinReqDTO requestParam) {
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUri())
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getGid, requestParam.getGid());
        LinkDO linkDO = LinkDO
                .builder()
                .enableStatus(0)
                .build();
        baseMapper.update(linkDO, updateWrapper);
        stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK, requestParam.getFullShortUri()));
    }

    @Override
    public void deleteRecycleBin(DeleteRecycleBinReqDTO requestParam) {
        LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                .eq(LinkDO::getEnableStatus, 1)
                .eq(LinkDO::getDelTime, 0L)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUri());
        LinkDO linkDO = LinkDO.builder()
                .delTime(System.currentTimeMillis())
                .build();
        linkDO.setDelFlag(1);
        baseMapper.update(linkDO, updateWrapper);
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        IPage<LinkDO> resultPage = baseMapper.pageRecycleBinLink(requestParam);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }
}