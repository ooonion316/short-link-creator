package pers.zyx.shortlink.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_link_goto")
public class LinkGotoDO {
    /**
     * id
     */
    private Long id;

    /**
     * 分组id
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}