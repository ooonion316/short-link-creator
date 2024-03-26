package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.UserDO;

public interface UserService extends IService<UserDO> {
    /**
     * 查看用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true, 不存在返回 false
     */
    Boolean hasUsername(String username);
}