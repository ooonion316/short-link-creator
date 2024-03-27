package pers.zyx.shortlink.dto.resp;

import lombok.Data;

/**
 * 查询短链接分组返回实体
 */
@Data
public class GroupListRespDTO {
    /**
     * 分组 id
     */
    private String gid;

    /**
     * 分组名
     */
    private String name;

    /**
     * 用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}