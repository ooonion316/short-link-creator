package pers.zyx.shortlink.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.zyx.shortlink.dao.entity.UserDO;
import pers.zyx.shortlink.dto.req.UserRegisterReqDTO;
import pers.zyx.shortlink.dto.req.UserUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.UserInfoRespDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 查看用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true, 不存在返回 false
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     *
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回实体
     */
    UserInfoRespDTO getUserByUsername(String username);

    /**
     * 用户信息修改
     *
     * @param requestParam 用户信息修改请求参数
     */
    void updateUser(UserUpdateReqDTO requestParam);
}