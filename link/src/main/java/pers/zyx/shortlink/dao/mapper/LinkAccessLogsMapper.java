package pers.zyx.shortlink.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.zyx.shortlink.dao.entity.LinkAccessLogsDO;
import pers.zyx.shortlink.dto.req.ShortLinkStatsReqDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 根据短链接获取指定日期内高频访问 IP 统计数据
     */
    @Select("""
            SELECT
            	ip,
            	COUNT(ip) AS count
            FROM t_link_access_logs
            WHERE
            	full_short_url = #{param.fullShortUrl}
            	AND gid = #{param.gid} 
            	AND create_time BETWEEN CONCAT(#{param.startDate}, ' 00:00:00') AND CONCAT(#{param.endDate}, ' 23:59:59')
            GROUP BY
            	full_short_url,
            	gid,
            	ip
            ORDER BY count DESC
            LIMIT 5
        """)
    List<HashMap<String, Object>> listTopIpByShortLink(@Param("param")ShortLinkStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内新旧访客统计数据
     */
    @Select("""
            SELECT
                SUM(old_user) AS oldUserCnt,
                SUM(new_user) AS newUserCnt
            FROM(SELECT
                     CASE WHEN COUNT(DISTINCT DATE(create_time)) > 1 THEN 1 ELSE 0 END AS old_user,
                     CASE WHEN COUNT(DISTINCT DATE(create_time)) = 1 AND MAX(DATE(create_time)) >= #{param.endDate} AND MAX(DATE(create_time)) <= #{param.endDate} THEN 1 ELSE 0 END AS new_user
                 from t_link_access_logs
                 where full_short_url = 'http://nurl.lnk/1xd7iO'
                   and gid = 'pwztz8'
                 group by user)
                    as user_counts
        """)
    HashMap<String, Object> findUvTypeCntByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据用户名查询用户是否为新旧访客
     */
    @Select("""
            <script>
            SELECT 
                user,
                CASE WHEN MIN(create_time) BETWEEN #{startDate} AND #{endDate} THEN '新访客' ELSE '老访客' END AS uvType
            FROM t_link_access_logs
            WHERE
                full_short_url = #{fullShortUrl} AND
                gid = #{gid} AND
                user IN
                <foreach item='item' index='index' collection='userAccessLogsList' open='(' separator=',' close=')'>
                    #{item}
                </foreach>
            GROUP BY user
            </script>
        """)
    List<Map<String, Object>> selectUvTypeByUsers(@Param("gid") String gid,
                                                  @Param("fullShortUrl") String fullShortUrl,
                                                  @Param("startDate") String startDate,
                                                  @Param("endDate") String endDate,
                                                  @Param("userAccessLogsList") List<String> userAccessLogsList);
}