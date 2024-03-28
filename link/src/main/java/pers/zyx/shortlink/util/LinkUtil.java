package pers.zyx.shortlink.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static pers.zyx.shortlink.constant.LinkEnableStatusConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * 短链接工具类
 */
public class LinkUtil {
    /**
     * 获取短链接有效时间
     *
     * @param validDate 有效期
     * @return 有效期时间戳
     */
    public static long getLinkCacheValidDate(Date validDate) {
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS))   // 获取当前时间和过期时间的差值
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }
}