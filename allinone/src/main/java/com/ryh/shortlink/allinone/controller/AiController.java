package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.service.AiService;
import com.ryh.shortlink.allinone.service.PermissionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final PermissionService permissionService;

    @GetMapping("/analyze")
    public Result<Map<String, Object>> analyze(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {

        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        Long userId = SessionUtils.getUserId(session);
        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "AI_ANALYZE");
            if (!hasPermission) {
                return Result.error("无权限使用AI分析");
            }
        }

        Map<String, Object> result = aiService.analyzeLinks(username, startDate, endDate);
        return Result.success(result);
    }

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, String> params, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("未登录");
        }

        Long userId = SessionUtils.getUserId(session);
        if (!SessionUtils.isAdmin(session)) {
            boolean hasPermission = permissionService.hasPermission(userId, "AI_ANALYZE");
            if (!hasPermission) {
                return Result.error("无权限使用AI对话");
            }
        }

        String message = params.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        String context = params.get("context");

        String response = aiService.chat(username, message, context);
        return Result.success(response);
    }
}
