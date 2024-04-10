package pers.zyx.shortlink.remote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.remote.LinkActualRemoteService;
import pers.zyx.shortlink.result.Result;

@RestController
@RequiredArgsConstructor
public class UrlTitleController {
    private final LinkActualRemoteService linkActualRemoteService;

    /**
     * 根据 URL 获取网站标题
     */
    @GetMapping("/api/short-link/admin/v1/title")
    public Result<String> getTitleByUrl(@RequestParam("url") String url) {
        return linkActualRemoteService.getTitleByUrl(url);
    }
}