package pers.zyx.shortlink.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.zyx.shortlink.dto.req.UserLoginReqDTO;
import pers.zyx.shortlink.dto.req.UserRegisterReqDTO;
import pers.zyx.shortlink.dto.req.UserUpdateReqDTO;
import pers.zyx.shortlink.dto.resp.UserActualInfoRespDTO;
import pers.zyx.shortlink.dto.resp.UserInfoRespDTO;
import pers.zyx.shortlink.dto.resp.UserLoginRespDTO;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.service.UserService;

@RestController
@RequestMapping("/api/short-link/admin/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查看用户名是否存在
     */
    @GetMapping("/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username") String username) {
        return Results.success(userService.hasUsername(username));
    }

    /**
     * 注册用户
     */
    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 根据用户名查询用户信息
     */
    @GetMapping("/{username}")
    public Result<UserInfoRespDTO> getUserByUsername(@PathVariable("username") String username) {
        UserInfoRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 根据用户名查询用户真实信息
     */
    @GetMapping("/actual/{username}")
    public Result<UserActualInfoRespDTO> getUserActualByUsername(@PathVariable("username") String username) {
        UserInfoRespDTO userInfoRespDTO = userService.getUserByUsername(username);
        UserActualInfoRespDTO result = BeanUtil.copyProperties(userInfoRespDTO, UserActualInfoRespDTO.class);
        return Results.success(result);
    }

    /**
     * 用户信息修改
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody UserUpdateReqDTO requestParam) {
        userService.updateUser(requestParam);
        return Results.success();
    }

    /**
     * 用户登陆
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        UserLoginRespDTO result = userService.login(requestParam);
        return Results.success(result);
    }

    /**
     * 检测用户是否登陆
     */
    @GetMapping("/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username") String username, @RequestParam("token") String token) {
        Boolean isLogin = userService.checkLogin(username, token);
        return Results.success(isLogin);
    }

    /**
     * 用户登出
     */
    @DeleteMapping
    public Result<Void> logout(@RequestParam("username") String username, @RequestParam("token") String token) {
        userService.logout(username, token);
        return Results.success();
    }
}