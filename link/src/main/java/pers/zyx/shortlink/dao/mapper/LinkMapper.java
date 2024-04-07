package pers.zyx.shortlink.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import pers.zyx.shortlink.dao.entity.LinkDO;
import pers.zyx.shortlink.dto.req.ShortLinkPageReqDTO;

public interface LinkMapper extends BaseMapper<LinkDO> {

    /**
     * 短链接访问统计自增
     */
    @Update("""
            UPDATE t_link
            SET
                total_pv = total_pv + #{totalPv},
                total_uv = total_uv + #{totalUv},
                total_uip = total_uip + #{totalUip}
            WHERE
                gid = #{gid} and
                full_short_url = #{fullShortUrl}
        """)
    void incrementStats(
            @Param("gid") String gid,
            @Param("fullShortUrl") String fullShortUrl,
            @Param("totalPv") Integer totalPv,
            @Param("totalUv") Integer totalUv,
            @Param("totalUip") Integer totalUip
    );

    /**
     * 分页统计短链接
     */
    IPage<LinkDO> pageLink(ShortLinkPageReqDTO requestParam);
}
