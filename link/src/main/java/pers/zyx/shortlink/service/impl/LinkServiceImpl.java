package pers.zyx.shortlink.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.Cookie;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.zyx.shortlink.dao.entity.*;
import pers.zyx.shortlink.dao.mapper.*;
import pers.zyx.shortlink.dto.req.ShortLinkBatchCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkCreateReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;
import pers.zyx.shortlink.dto.req.ShortLinkUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.*;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.service.LinkService;
import pers.zyx.shortlink.util.HashUtil;
import pers.zyx.shortlink.util.LinkUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static pers.zyx.shortlink.constant.LinkAccessStatsConstant.AMAP_REMOTE_URL;
import static pers.zyx.shortlink.constant.LinkAccessStatsRedisKeyConstant.UIP_SHORT_LINK;
import static pers.zyx.shortlink.constant.LinkAccessStatsRedisKeyConstant.UV_SHORT_LINK;
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
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;

    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocalAmapKey;

    @Value("${short-link.domain.default}")
    public String createShortLinkDefaultDomain;

    @Override
    @Transactional
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String suffix = generateSuffix(createShortLinkDefaultDomain, requestParam.getOriginUrl());
        if (suffix == "") throw new ClientException("访问人数过多, 请稍后再试");

        String fullShortUrl = createShortLinkDefaultDomain + "/" + suffix;

        LinkDO linkDO = BeanUtil.toBean(requestParam, LinkDO.class);
        linkDO.setDomain(createShortLinkDefaultDomain);
        linkDO.setFullShortUrl(fullShortUrl);
        linkDO.setShortUri(suffix);
        linkDO.setEnableStatus(0);
        linkDO.setTotalPv(0);
        linkDO.setTotalUv(0);
        linkDO.setTotalUip(0);
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

        String resultUrl = "http://" + linkDO.getDomain() + ":8001" + "/" + linkDO.getShortUri();

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(resultUrl)
                .originUrl(linkDO.getOriginUrl())
                .gid(linkDO.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<LinkDO> resultPage = baseMapper.pageLink(requestParam);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
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

        if (!Objects.equals(hasShortLinkDO.getValidDateType(), requestParam.getValidDateType())
                || !Objects.equals(hasShortLinkDO.getValidDate(), requestParam.getValidDate())) {
            stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK, requestParam.getFullShortUrl()));
            if (hasShortLinkDO.getValidDate() != null && hasShortLinkDO.getValidDate().before(new Date())) {
                if (Objects.equals(requestParam.getValidDateType(), PERMANENT) || requestParam.getValidDate().after(new Date())) {
                    stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK, requestParam.getFullShortUrl()));
                }
            }
        }
    }

    @Override
    @SneakyThrows
    public void restoreUri(String shortUri, HttpServletRequest request, HttpServletResponse response) {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        // 判断布隆过滤器
        if (!shortLinkBloomFilter.contains(fullShortUrl)) {
            response.sendRedirect("/page/notfound");
            return;
        }

        // 尝试从 Redis 中获取原始链接
        String originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK, fullShortUrl));
        if (Strings.isNotBlank(originLink)) {
            shortLinkStats(fullShortUrl, null, request, response);
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
                shortLinkStats(fullShortUrl, null, request, response);
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
            shortLinkStats(fullShortUrl, null, request, response);
            response.sendRedirect(linkDO.getOriginUrl());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        ArrayList<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        for(int i = 0; i < originUrls.size(); i++) {
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            try {
                ShortLinkCreateRespDTO shortLink = createShortLink(shortLinkCreateReqDTO);
                ShortLinkBaseInfoRespDTO linkBaseInfoRespDTO = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .describe(describes.get(i))
                        .build();
                result.add(linkBaseInfoRespDTO);
            } catch (Throwable ex) {
                log.error("批量创建短链接失败，原始参数：{}", originUrls.get(i));
            }
        }
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())
                .baseLinkInfos(result)
                .build();
    }

    private void shortLinkStats(String fullShortUrl, String gid, HttpServletRequest request, HttpServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();    // 第一次访问标识
        AtomicReference<String> uv = new AtomicReference<>();
        Cookie[] cookies = request.getCookies();
        try {
            // Uv stats
            Runnable addResponseCookieTesk = () -> {
                uv.set(UUID.randomUUID().toString(true));
                Cookie uvCookie = new Cookie("uv", uv.get());
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("."), fullShortUrl.length())); // 如果不设置, 则该域名下所有链接都使用这个cookie
                response.addCookie(uvCookie);
                uvFirstFlag.set(Boolean.TRUE);
                stringRedisTemplate.opsForSet().add(UV_SHORT_LINK + fullShortUrl, uv.get());
            };
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            uv.set(each);
                            Long uvAdded = stringRedisTemplate.opsForSet().add(UV_SHORT_LINK + fullShortUrl, each);
                            uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                        }, addResponseCookieTesk);
            } else {
                addResponseCookieTesk.run();
            }

            // Uip stats
            String remoteAddr = LinkUtil.getActualIp(request);
            Long uipAdded = stringRedisTemplate.opsForSet().add(UIP_SHORT_LINK + fullShortUrl, remoteAddr);
            boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;

            // Pv stats
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<LinkGotoDO> queryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                        .eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
                LinkGotoDO linkGotoDO = linkGotoMapper.selectOne(queryWrapper);
                gid = linkGotoDO.getGid();
            }
            Date date = new Date();
            int hour = DateUtil.hour(date, true);
            Week week = DateUtil.dayOfWeekEnum(date);
            int weekValue = week.getIso8601Value();

            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(uvFirstFlag.get() ? 1 : 0)
                    .uip(uipFirstFlag ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(date)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            // Local stats
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocalAmapKey);
            localeParamMap.put("ip", remoteAddr);
            String localResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localResultObj = JSON.parseObject(localResultStr);
            String infoCode = localResultObj.getString("infocode");
            LinkLocaleStatsDO linkLocaleStatsDO;
            String actualProvince = "未知";
            String actualCity = "未知";
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .province(actualProvince = unknownFlag ? "未知" : province)
                        .city(actualCity = unknownFlag ? "未知" : localResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localResultObj.getString("adcode"))
                        .country("中国")
                        .gid(gid)
                        .cnt(1)
                        .date(date)
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }

            // Os stats
            String os = LinkUtil.getOs(request);
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .os(os)
                    .gid(gid)
                    .cnt(1)
                    .date(date)
                    .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);

            // Browser stats
            String browser = LinkUtil.getBrowser(request);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .browser(browser)
                    .gid(gid)
                    .cnt(1)
                    .date(date)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);

            // Device stats
            String device = LinkUtil.getDevice(request);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(device)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);

            // Network stats
            String network = LinkUtil.getNetwork(request);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(network)
                    .cnt(1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);


            // Frequency IP stats
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .ip(remoteAddr)
                    .browser(browser)
                    .os(os)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .user(uv.get())
                    .network(network)
                    .device(device)
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);
        } catch (Throwable ex) {
            log.error("短链接访问统计异常", ex);
        }
    }

    public String generateSuffix(String domain, String originUrl) {
        String suffix = "";
        for(int i = 0; i < 10; i++) {
            suffix = originUrl + System.currentTimeMillis();
            suffix = HashUtil.hashToBase62(suffix);
            if (!shortLinkBloomFilter.contains(domain + "/" + suffix)) break;
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
