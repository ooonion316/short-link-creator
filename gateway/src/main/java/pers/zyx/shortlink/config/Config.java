package pers.zyx.shortlink.config;

import lombok.Data;

import java.util.List;

/**
 * 过滤器配置
 */
@Data
public class Config {
    /**
     * 白名单前缀路径
     */
    private List<String> whitePathList;
}
