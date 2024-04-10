package pers.zyx.shortlink.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pers.zyx.shortlink.config.Config;
import pers.zyx.shortlink.dto.GateWayErrorResult;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * SpringCloud Gateway Token 拦截器
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate) {
        super(Config.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String requestPath = request.getPath().toString();
            String requestMethod = request.getMethod().name();
            if (!isPathInWhiteList(requestPath, requestMethod, config.getWhitePathList())) {
                String username = request.getHeaders().getFirst("username");
                String token = request.getHeaders().getFirst("token");
                Object userInfo;
                if (StringUtils.hasText(username) && StringUtils.hasText(token) && (userInfo = stringRedisTemplate.opsForHash().get("short-link:logged_in:" + username, token)) != null) {
                    stringRedisTemplate.expire("short-link:logged_in:" + username, 30L, TimeUnit.MINUTES);
                    JSONObject userInfoJsonObject = JSON.parseObject(userInfo.toString());
                    ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                        httpHeaders.set("userId", userInfoJsonObject.getString("id"));
                        httpHeaders.set("realName", URLEncoder.encode(userInfoJsonObject.getString("realName"), StandardCharsets.UTF_8));
                    });
                    return chain.filter(exchange.mutate().request(builder.build()).build());
                }
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    GateWayErrorResult resultMessage = GateWayErrorResult.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Token validation error")
                            .build();
                    return bufferFactory.wrap(JSON.toJSONString(resultMessage).getBytes());
                }));
            }
            return chain.filter(exchange);
        };
    }

    private boolean isPathInWhiteList(String requestPath, String requestMethod, List<String> whitePathList) {
        return (!CollectionUtils.isEmpty(whitePathList) && whitePathList.stream().anyMatch(requestPath::startsWith)) || (Objects.equals(requestPath, "/api/short-link/admin/v1/user") && Objects.equals(requestMethod, "POST"));
    }
}