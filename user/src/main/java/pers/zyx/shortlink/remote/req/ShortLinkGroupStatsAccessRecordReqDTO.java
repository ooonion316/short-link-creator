package pers.zyx.shortlink.remote.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 *分组内所有短链接指定时间内访问记录监控数据请求参数
 */
@Data
public class ShortLinkGroupStatsAccessRecordReqDTO extends Page {

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
