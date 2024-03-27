package pers.zyx.shortlink.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pers.zyx.shortlink.exception.ClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户拦截器
 */
@RequiredArgsConstructor
public class UserInterception implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    private static final String USER_LOGIN_PREFIX = "short-link:logged_in:";

    private static final List<String> EXCLUDE_URLS = Arrays.asList("/api/short-link/admin/v1/user",
                                                                   "/api/short-link/admin/v1/user/has-username",
                                                                   "/api/short-link/admin/v1/user/login");

    private static final List<String> EXCLUDE_METHODS = Arrays.asList("POST", "GET", "POST");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        for (int i = 0; i < method.length(); i++) {
            if ((StrUtil.equals(method, EXCLUDE_METHODS.get(i))) && (StrUtil.equals(requestURI, EXCLUDE_URLS.get(i)))) {
                return true;
            }
        }

        String username = request.getHeader("username");
        String token = request.getHeader("token");
        Object userInfo = null;
        try {
            userInfo = stringRedisTemplate.opsForHash().get(USER_LOGIN_PREFIX + username, token);
        } catch (Exception e) {
            throw new ClientException("用户名不存在");
        }
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
