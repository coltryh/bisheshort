package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 短链接跳转控制器
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final ShortLinkService shortLinkService;

    /**
     * 短链接跳转
     */
    @GetMapping("/{shortUri}")
    public void redirect(@PathVariable String shortUri, HttpServletRequest request, HttpServletResponse response) {
        try {
            String originalUrl = shortLinkService.restoreUrl(shortUri);
            if (originalUrl != null) {
                // 记录访问日志（简化版，实际项目可能需要异步处理）
                log.info("短链接跳转: {} -> {}", shortUri, originalUrl);
                response.sendRedirect(originalUrl);
            } else {
                // 短链接不存在或已过期
                log.warn("短链接不存在: {}", shortUri);
                response.sendRedirect("/page/notfound");
            }
        } catch (IOException e) {
            log.error("短链接跳转失败: {}", shortUri, e);
            try {
                response.sendRedirect("/page/error");
            } catch (IOException ex) {
                // ignore
            }
        }
    }
}
