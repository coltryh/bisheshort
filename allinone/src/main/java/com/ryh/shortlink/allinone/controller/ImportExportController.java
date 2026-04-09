package com.ryh.shortlink.allinone.controller;

import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBaseInfoRespDTO;
import com.ryh.shortlink.allinone.service.ImportExportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 导入导出控制器
 */
@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
public class ImportExportController {

    private final ImportExportService importExportService;

    /**
     * 导出短链接为CSV
     */
    @GetMapping("/export/{gid}")
    public Result<String> exportCsv(@PathVariable String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            String csvContent = importExportService.exportToCsv(gid);
            return Result.success(csvContent);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从CSV导入短链接
     */
    @PostMapping("/import/{gid}")
    public Result<List<ShortLinkBaseInfoRespDTO>> importCsv(
            @PathVariable String gid,
            @RequestBody Map<String, String> requestParam,
            HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            String csvContent = requestParam.get("csvContent");
            if (csvContent == null || csvContent.isEmpty()) {
                return Result.error("CSV内容不能为空");
            }
            List<ShortLinkBaseInfoRespDTO> results = importExportService.importFromCsv(gid, csvContent);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
