package pers.zyx.shortlink.dto.req;

import lombok.Data;

/**
 * 排序短链接分组
 */
@Data
public class GroupSortReqDTO {
    /**
     * 分组id
     */
    private String gid;
    /**
     * 排序标识
     */
    private Integer sortOrder;
}