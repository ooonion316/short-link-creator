package pers.zyx.shortlink.dto.req;

import lombok.Data;

/**
 * 保存短链接分组
 */
@Data
public class GroupSaveReqDTO {
    /**
     * 分组名
     */
    private String name;
}