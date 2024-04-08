package pers.zyx.shortlink.interceptor;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import pers.zyx.shortlink.biz.user.UserContext;
import pers.zyx.shortlink.config.UserFlowRiskControlConfiguration;
import pers.zyx.shortlink.exception.ClientException;
import pers.zyx.shortlink.result.Results;

import java.io.PrintWriter;
import java.util.Optional;

import static pers.zyx.shortlink.errorcode.BaseErrorCode.FLOW_LIMIT_ERROR;

@Slf4j
@Order(10)
@RequiredArgsConstructor
public class UserFlowRiskControlInterceptor implements AsyncHandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;
    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_flow_risk_control.lua";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (userFlowRiskControlConfiguration.getEnable().equals(false)) return true;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        redisScript.setResultType(Long.class);
        String username = Optional.ofNullable(UserContext.getUsername()).orElse("other");
        Long result = null;
        try {
            result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(username), userFlowRiskControlConfiguration.getTimeWindow());
        } catch (Throwable ex) {
            log.error("执行用户请求流量限制LUA脚本出错", ex);
            returnJson(response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }
        if (result == null || result > userFlowRiskControlConfiguration.getMaxAccessCount()) {
            returnJson(response, JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
        }
        return true;
    }

    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}
