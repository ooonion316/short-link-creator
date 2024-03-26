package pers.zyx.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}