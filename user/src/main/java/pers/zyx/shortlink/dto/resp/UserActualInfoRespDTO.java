package pers.zyx.shortlink.dto.resp;

import lombok.Data;

/**
 * 查询用户信息返回实体
 */
@Data
public class UserActualInfoRespDTO {
    /**
     * Id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}