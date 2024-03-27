package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.GroupDO;
import pers.zyx.shortlink.dto.req.GroupSaveReqDTO;
import pers.zyx.shortlink.dto.req.GroupSortReqDTO;
import pers.zyx.shortlink.dto.req.GroupUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.GroupListRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    /**
     * 新增短链接分组
     *
     * @param requestParam 新增分组请求参数
     */
    void saveGroup(GroupSaveReqDTO requestParam);

    /**
     * 查询短链接分组
     *
     * @return 短链接分组信息
     */
    List<GroupListRespDTO> listGroup();

    /**
     * 更新短链接分组
     *
     * @param requestParam 更新参数
     */
    void updateGroup(GroupUpdateReqDTO requestParam);

    /**
     * 删除短链接分组
     *
     * @param gid 分组 id
     */
    void removeGroup(String gid);

    /**
     * 短链接分组排序
     *
     * @param requestParam 分组排序请求参数
     */
    void sortGroup(List<GroupSortReqDTO> requestParam);
}
