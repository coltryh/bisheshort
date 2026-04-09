package com.ryh.shortlink.allinone.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ryh.shortlink.allinone.common.annotation.RequireRole;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkPageRespDTO;
import com.ryh.shortlink.allinone.service.GroupService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 回收站控制器
 */
@RestController
@RequestMapping("/api/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {

    private final ShortLinkService shortLinkService;
    private final GroupService groupService;

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/list")
    @RequireRole("admin")
    public Result<IPage<ShortLinkPageRespDTO>> list(
            @RequestParam(required = false, defaultValue = "1") Long current,
            @RequestParam(required = false, defaultValue = "10") Long size,
            HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            // 获取用户的所有分组
            var groups = groupService.listByUsername(username);
            List<String> gidList = groups.stream()
                    .map(g -> g.getGid())
                    .toList();

            IPage<ShortLinkPageRespDTO> page = shortLinkService.pageRecycleBinShortLink(gidList, current, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 恢复短链接
     */
    @PostMapping("/recover/{id}")
    @RequireRole("admin")
    public Result<?> recover(@PathVariable Long id, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            shortLinkService.recoverShortLinkById(id, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 永久删除短链接
     */
    @DeleteMapping("/remove/{id}")
    @RequireRole("admin")
    public Result<?> remove(@PathVariable Long id, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            shortLinkService.removeShortLinkById(id, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
