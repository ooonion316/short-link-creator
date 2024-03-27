package pers.zyx.shortlink.dto.resp;

import lombok.Data;

/**
 * 短链接数量
 */
@Data
public class ShortLinkGroupCountRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;
}