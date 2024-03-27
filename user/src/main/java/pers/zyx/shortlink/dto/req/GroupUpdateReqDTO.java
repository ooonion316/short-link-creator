package pers.zyx.shortlink.dto.req;

import lombok.Data;

/**
 * 更新短链接分组请求参数
 */
@Data
public class GroupUpdateReqDTO {
    /**
     * 分组 id
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;
}