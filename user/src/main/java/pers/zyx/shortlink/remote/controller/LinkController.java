package pers.zyx.shortlink.remote.controller;

import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.service.LinkRemoteService;

/**
 * 链接中台调用, 后期使用 SpringCloud 代替
 */
@RestController
public class LinkController {
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };
}
