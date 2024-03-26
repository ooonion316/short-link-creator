package pers.zyx.shortlink.biz.user;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 用户拦截器
 */
@RequiredArgsConstructor
public class UserInterception implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    private static final String USER_LOGIN_PREFIX = "short-link:login_";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = request.getHeader("username");
        String token = request.getHeader("token");
        Object userInfo = stringRedisTemplate.opsForHash().get(USER_LOGIN_PREFIX + username, token);
        if (Objects.nonNull(userInfo)) {
            UserInfoDTO userInfoDTO = JSON.parseObject(userInfo.toString(), UserInfoDTO.class);
            UserContext.setUser(userInfoDTO);
        } else {
            response.setStatus(401);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.removeUser();
    }
}
