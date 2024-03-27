package pers.zyx.shortlink.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.zyx.shortlink.dto.req.GroupSaveReqDTO;
import pers.zyx.shortlink.result.Result;
import pers.zyx.shortlink.result.Results;
import pers.zyx.shortlink.service.GroupService;

@RestController
@RequestMapping("/api/short-link/admin/v1/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping
    public Result<Void> saveGroup(@RequestBody GroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam);
        return Results.success();
    }
}
