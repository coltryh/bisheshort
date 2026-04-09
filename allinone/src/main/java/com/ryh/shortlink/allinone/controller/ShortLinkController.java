package com.ryh.shortlink.allinone.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dto.req.ShortLinkCreateReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkPageReqDTO;
import com.ryh.shortlink.allinone.dto.req.ShortLinkUpdateReqDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBatchCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkCreateRespDTO;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkPageRespDTO;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 短链接控制器
 */
@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;

    /**
     * 分页查询短链接
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            IPage<ShortLinkPageRespDTO> page = shortLinkService.pageShortLink(requestParam);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据分组查询短链接列表
     */
    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String gid, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            var links = shortLinkService.listByGid(gid);
            return Result.success(links);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取短链接详情
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            var link = shortLinkService.getById(id);
            if (link == null) {
                return Result.error("短链接不存在");
            }
            return Result.success(link);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建短链接
     */
    @PostMapping("/save")
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            ShortLinkCreateRespDTO result = shortLinkService.createShortLink(requestParam);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量创建短链接
     */
    @PostMapping("/save/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreate(@RequestBody Map<String, Object> requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            @SuppressWarnings("unchecked")
            List<String> originUrls = (List<String>) requestParam.get("originUrls");
            @SuppressWarnings("unchecked")
            List<String> describes = (List<String>) requestParam.get("describes");
            String gid = (String) requestParam.get("gid");
            Integer createdType = (Integer) requestParam.get("createdType");

            ShortLinkBatchCreateRespDTO result = shortLinkService.batchCreateShortLink(originUrls, describes, gid, createdType);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新短链接
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody ShortLinkUpdateReqDTO requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            shortLinkService.updateShortLink(requestParam);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除短链接到回收站
     */
    @DeleteMapping("/delete/{gid}/{fullShortUrl}")
    public Result<?> delete(@PathVariable String gid, @PathVariable String fullShortUrl, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        try {
            shortLinkService.deleteToRecycleBin(gid, fullShortUrl, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
