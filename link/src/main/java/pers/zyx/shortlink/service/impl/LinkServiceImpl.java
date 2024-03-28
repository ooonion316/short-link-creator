package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dao.mapper.LinkMapper;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkCreateRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkGroupCountRespDTO;
import pers.zyx.shortlink.dto.resp.ShortLinkPageRespDTO;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.service.LinkService;
import pers.zyx.shortlink.util.HashUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static pers.zyx.shortlink.constant.LinkEnableStatusConstant.ENABLE;
import static pers.zyx.shortlink.constant.LinkEnableStatusConstant.PERMANENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
    private final RBloomFilter<String> shortLinkBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(requestParam.getDomain(), requestParam.getOriginUrl());
        if (suffix == "") throw new ClientException("访问人数过多, 请稍后再试");

        String fullShortUrl = requestParam.getDomain() + "/" + suffix;

        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);

        try {
            baseMapper.insert(linkDO);
        } catch (DuplicateKeyException ex) {
            log.warn("短链接 {} 重复入库", fullShortUrl);
        }
        shortLinkBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(linkDO.getFullShortUrl())
                .originUrl(linkDO.getOriginUrl())
                .gid(linkDO.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getEnableStatus, 0);
        IPage<LinkDO> resultPage = baseMapper.selectPage(requestParam, queryWrapper);
        return resultPage.convert(each ->BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> countGroupShortLink(List<String> gids) {
        QueryWrapper<LinkDO> queryWrapper = Wrappers.query(new LinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkGroupCountRespDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getEnableStatus, ENABLE)
                .eq(LinkDO::getGid, requestParam.getGid())
                .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl());
        LinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接不存在");
        }

        LinkDO linkDO = LinkDO.builder()
                .gid(hasShortLinkDO.getGid())
                .domain(hasShortLinkDO.getDomain())
                .favicon(hasShortLinkDO.getFavicon())
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                .createdType(hasShortLinkDO.getCreatedType())
                .describe(requestParam.getDescribe())
                .validDate(requestParam.getValidDate())
                .originUrl(requestParam.getOriginUrl())
                .validDateType(requestParam.getValidDateType())
                .build();

        // Gid 是否一致, 一致直接修改, 否则先删除再添加
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(LinkDO::getGid, requestParam.getGid())
                    .eq(LinkDO::getEnableStatus, ENABLE)
                    .set(Objects.equals(requestParam.getValidDateType(), PERMANENT), LinkDO::getValidDate, null);
            baseMapper.update(linkDO, updateWrapper);
        } else {
            LambdaQueryWrapper<LinkDO> deleteWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(LinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(LinkDO::getEnableStatus, 0);
            linkDO.setGid(requestParam.getGid());
            baseMapper.delete(deleteWrapper);
            baseMapper.insert(linkDO);
        }
    }

    public String generateSuffix(String domain, String originUrl) {
        String suffix = "";
        for(int i = 0; i < 10; i++) {
            suffix = originUrl + System.currentTimeMillis();
            suffix = HashUtil.hashToBase62(suffix);
            if (!shortLinkBloomFilter.contains(domain + "/" + suffix)) break;;
        }
        return suffix;
    }
}