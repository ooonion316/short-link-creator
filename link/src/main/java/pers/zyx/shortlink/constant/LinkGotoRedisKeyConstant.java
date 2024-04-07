package pers.zyx.shortlink.constant;

/**
 * 短链接跳转 Redis 前缀
 */
public class LinkGotoRedisKeyConstant {
    /**
     * 短链接正常跳转
     */
    public static final String GOTO_SHORT_LINK = "short-link:goto:%s";

    /**
     * 短链接跳转为空
     */
    public static final String GOTO_IS_NULL_SHORT_LINK = "short-link:goto_is_null:%s";

    /**
     * 短链接重建锁前缀
     */
    public static final String GOTO_SHORT_LINK_LOCK = "short-link:lock_link_goto:%s";

    /**
     * 短链接修改分组 ID 锁前缀
     */
    public static final String GID_UPDATE_KEY_LOCK = "short-link:lock_update-gid:%s";

    /**
     * 短链接延迟队列消费统计前缀
     */
    public static final String DELAY_QUEUE_STATS_KEY = "short-link:delay-queue:stats";
}
