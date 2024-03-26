package pers.zyx.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import pers.zyx.shortlink.service.UserService;

@RestController
@RequestMapping("/api/short-link/admin/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}