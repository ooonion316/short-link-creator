package pers.zyx.shortlink.dto.req;

import lombok.Data;

/**
 * 将短链接移出回收站请求参数
 */
@Data
public class RecoverRecycleBinReqDTO {
    /**
     * 分组 id
     */
    private String gid;

    /**
     * 全部短链接
     */
    private String fullShortUri;
}