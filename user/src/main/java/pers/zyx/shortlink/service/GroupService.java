package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.GroupDO;
import pers.zyx.shortlink.dto.req.GroupSaveReqDTO;
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
}
