package pers.zyx.shortlink.constant;

/**
 * 用户模块 Redis 缓存常量
 */
public class UserRedisCacheConstant {
    /**
     * 用户注册锁前缀
     */
    public static final String USER_REGISTER_LOCK = "short-link:lock_user_register:";

    /**
     * 用户登录前缀
     */
    public static final String USER_LOGIN_PREFIX = "short-link:logged_in:";

    /**
     * 链接分组创建锁前缀
     */
    public static final String GROUP_CREATE_LOCK = "short-link:lock_group-create:%s";
}
