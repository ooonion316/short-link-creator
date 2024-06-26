package pers.zyx.shortlink.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关错误返回信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GateWayErrorResult {

    /**
     * HTTP 状态码
     */
    private Integer status;

    /**
     * 返回信息
     */
    private String message;
}
