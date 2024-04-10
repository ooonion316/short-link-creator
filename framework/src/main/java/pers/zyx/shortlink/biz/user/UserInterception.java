package pers.zyx.shortlink.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.result.Results;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户拦截器
 */
@Order(0)
@RequiredArgsConstructor
public class UserInterception implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String username = request.getHeader("username");
        if(StrUtil.isNotBlank(username)) {
            String userId = request.getHeader("userId");
            String realName = request.getHeader("realName");
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, username, realName);
            UserContext.setUser(userInfoDTO);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserContext.removeUser();
    }
}
