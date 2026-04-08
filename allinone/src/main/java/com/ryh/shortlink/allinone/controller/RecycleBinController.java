package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {

    private final ShortLinkService shortLinkService;

    @GetMapping("/list")
    public Result<List<ShortLinkDO>> list(HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }
        List<ShortLinkDO> links = shortLinkService.listRecycleBin(username);
        return Result.success(links);
    }

    @PostMapping("/recover/{id}")
    public Result<?> recover(@PathVariable Long id, HttpSession session) {
        if (SessionUtils.getUserId(session) == null) {
            return Result.error("未登录");
        }
        boolean success = shortLinkService.recover(id);
        return success ? Result.success() : Result.error("恢复失败");
    }

    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable Long id, HttpSession session) {
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限");
        }
        boolean success = shortLinkService.permanentDelete(id);
        return success ? Result.success() : Result.error("删除失败");
    }
}
