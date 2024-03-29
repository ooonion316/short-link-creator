package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dao.entity.LinkGotoDO;
import pers.zyx.shortlink.dao.mapper.LinkGotoMapper;
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
import pers.zyx.shortlink.util.LinkUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static pers.zyx.shortlink.constant.LinkEnableStatusConstant.ENABLE;
import static pers.zyx.shortlink.constant.LinkEnableStatusConstant.PERMANENT;
import static pers.zyx.shortlink.constant.LinkGotoRedisKeyConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, LinkDO> implements LinkService {
    private final RBloomFilter<String> shortLinkBloomFilter;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;
    private final LinkGotoMapper linkGotoMapper;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(requestParam.getDomain(), requestParam.getOriginUrl());
        if (suffix == "") throw new ClientException("访问人数过多, 请稍后再试");

        String fullShortUrl = requestParam.getDomain() + "/" + suffix;

        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);
        LinkGotoDO linkGotoDO = LinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();

        try {
            baseMapper.insert(linkDO);
            linkGotoMapper.insert(linkGotoDO);
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
                .favicon(getFavicon(requestParam.getOriginUrl()))
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

    @Override
    @SneakyThrows
    public void restoreUri(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        String fullShortUrl = scheme + "://" + serverName + "/" + shortUri;

        // 判断布隆过滤器
        if (!shortLinkBloomFilter.contains(fullShortUrl)) {
            response.sendRedirect("/page/notfound");
            return;
        }

        // 尝试从 Redis 中获取原始链接
        String originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK, fullShortUrl));
        if (Strings.isNotBlank(originLink)) {
            response.sendRedirect(originLink);
            return;
        }

        // Redis 中不存在, 尝试重建索引
        RLock lock = redissonClient.getLock(String.format(GOTO_SHORT_LINK_LOCK, fullShortUrl));
        lock.lock();
        try {
            // 双重判定锁
            originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK, fullShortUrl));
            if (Strings.isNotBlank(originLink)) {
                response.sendRedirect(originLink);
                return;
            }

            // 短链接为空值, 直接返回 notfound
            String gotoIsNull = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK, fullShortUrl));
            if (Strings.isNotBlank(gotoIsNull)) {
                response.sendRedirect("/page/notfound");
                return;
            }

            // 在 goto 表中查询, 如果没查到缓存空值
            LambdaQueryWrapper<LinkGotoDO> gotoQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                    .eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
            LinkGotoDO linkGotoDO = linkGotoMapper.selectOne(gotoQueryWrapper);
            if (linkGotoDO == null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK, fullShortUrl), "null", 30L, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }

            LambdaQueryWrapper<LinkDO> linkQueryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getEnableStatus, ENABLE)
                    .eq(LinkDO::getGid, linkGotoDO.getGid())
                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
            LinkDO linkDO = baseMapper.selectOne(linkQueryWrapper);
            if (linkDO == null || (linkDO.getValidDate() != null && linkDO.getValidDate().before(new Date()))) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK, fullShortUrl), "null", 30L, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }

            // 重建缓存
            stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK, fullShortUrl), linkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidDate(linkDO.getValidDate()), TimeUnit.MILLISECONDS);
            response.sendRedirect(linkDO.getOriginUrl());
        } finally {
            lock.unlock();
        }

        LambdaQueryWrapper<LinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                .eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
        LinkGotoDO linkGotoDO = linkGotoMapper.selectOne(linkGotoQueryWrapper);
        if (linkGotoDO == null) {
            // TODO 可能是恶意访问, 进行风控
        }

        LambdaQueryWrapper<LinkDO> linkQueryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getEnableStatus, ENABLE)
                .eq(LinkDO::getGid, linkGotoDO.getGid())
                .eq(LinkDO::getFullShortUrl, linkGotoDO.getFullShortUrl());
        LinkDO linkDO = baseMapper.selectOne(linkQueryWrapper);
        if (linkDO != null) {
                response.sendRedirect(linkDO.getOriginUrl());
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

    @SneakyThrows
    public String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon").first();
            if (faviconLink != null) return faviconLink.attr("abs:href");
        }
        return null;
    }
}
