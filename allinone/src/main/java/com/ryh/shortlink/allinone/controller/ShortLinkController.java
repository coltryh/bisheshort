package com.ryh.shortlink.allinone.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ryh.shortlink.allinone.common.result.Result;
import com.ryh.shortlink.allinone.common.utils.SessionUtils;
import com.ryh.shortlink.allinone.dao.entity.LinkStatsTodayDO;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.mapper.LinkStatsTodayMapper;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 短链接控制器
 */
@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final LinkStatsTodayMapper linkStatsTodayMapper;

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

            // 获取今日日期
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();

            // 获取所有短链接的完整URL
            List<String> fullShortUrls = links.stream()
                    .map(ShortLinkDO::getFullShortUrl)
                    .collect(Collectors.toList());

            // 批量查询今日统计
            Map<String, LinkStatsTodayDO> todayStatsMap = new HashMap<>();
            if (!fullShortUrls.isEmpty()) {
                List<LinkStatsTodayDO> todayStatsList = linkStatsTodayMapper.selectByFullShortUrlsAndDate(fullShortUrls, today);
                for (LinkStatsTodayDO stat : todayStatsList) {
                    todayStatsMap.put(stat.getFullShortUrl(), stat);
                }
            }

            // 转换为PageRespDTO并填充今日统计
            List<ShortLinkPageRespDTO> result = links.stream().map(link -> {
                ShortLinkPageRespDTO dto = BeanUtil.toBean(link, ShortLinkPageRespDTO.class);
                LinkStatsTodayDO todayStat = todayStatsMap.get(link.getFullShortUrl());
                if (todayStat != null) {
                    dto.setTodayPv(todayStat.getTodayPv() != null ? todayStat.getTodayPv() : 0);
                    dto.setTodayUv(todayStat.getTodayUv() != null ? todayStat.getTodayUv() : 0);
                    dto.setTodayUip(todayStat.getTodayUip() != null ? todayStat.getTodayUip() : 0);
                } else {
                    dto.setTodayPv(0);
                    dto.setTodayUv(0);
                    dto.setTodayUip(0);
                }
                return dto;
            }).collect(Collectors.toList());

            return Result.success(result);
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
    @PostMapping(value = {"/save", "/create"})
    public Result<ShortLinkCreateRespDTO> create(@RequestBody ShortLinkCreateReqDTO requestParam, HttpSession session) {
        String username = SessionUtils.getUsername(session);
        if (username == null) {
            return Result.error("请先登录");
        }
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限操作，只有管理员可以创建短链接");
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
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限操作，只有管理员可以创建短链接");
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
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限操作，只有管理员可以修改短链接");
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
        if (!SessionUtils.isAdmin(session)) {
            return Result.error("无权限操作，只有管理员可以删除短链接");
        }
        try {
            shortLinkService.deleteToRecycleBin(gid, fullShortUrl, username);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
