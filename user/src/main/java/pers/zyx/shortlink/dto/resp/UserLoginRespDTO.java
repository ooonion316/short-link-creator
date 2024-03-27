package pers.zyx.shortlink.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录接口返回参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRespDTO {
    /**
     * 用户 Token
     */
    private String token;
}