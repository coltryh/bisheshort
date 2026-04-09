package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupSortReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkGroupUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkGroupRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 分组控制器
 */
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 查询用户的所有分组（带短链接数量）
     */
    @GetMapping("/list")
    public Result<List<ShortLinkGroupRespDTO>> list(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            List<ShortLinkGroupRespDTO> groups = groupService.listGroup(username);
            return Result.success(groups);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建分组
     */
    @PostMapping("/save")
    public Result<?> save(@RequestBody Map<String, String> requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            String groupName = requestParam.get("name");
            if (groupName == null || groupName.isEmpty()) {
                return Result.error("分组名称不能为空");
            }
            groupService.saveGroup(username, groupName);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新分组
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody ShortLinkGroupUpdateReqDTO requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            groupService.updateGroup(requestParam);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/delete/{gid}")
    public Result<?> delete(@PathVariable String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            groupService.deleteGroup(gid, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 排序分组
     */
    @PutMapping("/sort")
    public Result<?> sort(@RequestBody List<ShortLinkGroupSortReqDTO> requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            groupService.sortGroup(requestParam, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
