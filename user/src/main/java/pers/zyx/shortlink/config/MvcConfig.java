package pers.zyx.shortlink.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pers.zyx.shortlink.biz.user.UserInterception;
import pers.zyx.shortlink.interceptor.UserFlowRiskControlInterceptor;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final StringRedisTemplate stringRedisTemplate;
    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterception(stringRedisTemplate));
        registry.addInterceptor(new UserFlowRiskControlInterceptor(stringRedisTemplate, userFlowRiskControlConfiguration));
    }
}
