package pers.zyx.shortlink.remote.req;

import lombok.Data;

/**
 * 将链接从回收站中删除请求参数
 */
@Data
public class DeleteRecycleBinReqDTO {
    /**
     * 分组 id
     */
    private String gid;

    /**
     * 全部短链接
     */
    private String fullShortUri;
}