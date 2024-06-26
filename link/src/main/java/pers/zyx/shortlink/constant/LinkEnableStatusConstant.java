package pers.zyx.shortlink.constant;

/**
 * 短链接操作常量
 */
public class LinkEnableStatusConstant {
    /**
     * 短链接缓存默认有效时间
     */
    public static final long DEFAULT_CACHE_VALID_TIME = 2626560000L;

    /**
     * 已启用
     */
    public static final Integer ENABLE = 0;

    /**
     * 未启用
     */
    public static final Integer NOT_ENABLED = 1;

    /**
     * 永久有效期
     */
    public static final Integer PERMANENT = 0;

    /**
     * 自定义有效期
     */
    public static final Integer CUSTOM = 1;
}
