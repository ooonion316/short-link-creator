package pers.zyx.shortlink.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 原始链接白名单配置文件
 */
@Data
@Component
@ConfigurationProperties(prefix = "short-link.goto-domain.while-list")
public class GotoDomainWhileListConfiguration {
    /**
     * 是否开启白名单验证
     */
    private Boolean enable;

    /**
     * 白名单网站名称集合
     */
    private String names;

    /**
     * 白名单网站原始域名
     */
    private List<String> details;

}
