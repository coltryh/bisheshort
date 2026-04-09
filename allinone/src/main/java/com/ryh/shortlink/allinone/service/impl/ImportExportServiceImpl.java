package com.ryh.shortlink.allinone.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ryh.shortlink.allinone.dao.entity.ShortLinkDO;
import com.ryh.shortlink.allinone.dao.mapper.ShortLinkMapper;
import com.ryh.shortlink.allinone.dto.resp.ShortLinkBaseInfoRespDTO;
import com.ryh.shortlink.allinone.service.ImportExportService;
import com.ryh.shortlink.allinone.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导入导出服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkService shortLinkService;

    @Override
    public String exportToCsv(String gid) {
        List<ShortLinkDO> shortLinks = shortLinkMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ShortLinkDO>()
                        .eq(ShortLinkDO::getGid, gid)
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0)
        );

        StringBuilder csv = new StringBuilder();
        csv.append("原始链接,描述,创建时间,总PV,总UV,总UIP\n");

        for (ShortLinkDO link : shortLinks) {
            csv.append(escapeCsv(link.getOriginUrl())).append(",");
            csv.append(escapeCsv(link.getDescribe() != null ? link.getDescribe() : "")).append(",");
            csv.append(link.getCreateTime() != null ? link.getCreateTime().toString() : "").append(",");
            csv.append(link.getTotalPv() != null ? link.getTotalPv().toString() : "0").append(",");
            csv.append(link.getTotalUv() != null ? link.getTotalUv().toString() : "0").append(",");
            csv.append(link.getTotalUip() != null ? link.getTotalUip().toString() : "0").append("\n");
        }

        return csv.toString();
    }

    @Override
    public List<ShortLinkBaseInfoRespDTO> importFromCsv(String gid, String csvContent) {
        List<ShortLinkBaseInfoRespDTO> results = new ArrayList<>();

        if (StrUtil.isBlank(csvContent)) {
            return results;
        }

        String[] lines = csvContent.split("\n");
        int lineCount = 0;

        for (String line : lines) {
            lineCount++;
            // 跳过表头
            if (lineCount == 1 && line.contains("原始链接")) {
                continue;
            }

            if (StrUtil.isBlank(line)) {
                continue;
            }

            try {
                String[] fields = parseCsvLine(line);
                if (fields.length < 1 || StrUtil.isBlank(fields[0])) {
                    continue;
                }

                String originUrl = fields[0].trim();
                String describe = fields.length > 1 ? fields[1].trim() : "";

                // 创建短链接
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .originUrl(originUrl)
                        .describe(describe)
                        .gid(gid)
                        .createdType(1)  // 控制台创建
                        .validDateType(0)  // 永久有效
                        .enableStatus(0)
                        .totalPv(0)
                        .totalUv(0)
                        .totalUip(0)
                        .delFlag(0)
                        .delTime(0L)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();

                shortLinkMapper.insert(shortLinkDO);

                ShortLinkBaseInfoRespDTO respDTO = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLinkDO.getFullShortUrl())
                        .originUrl(originUrl)
                        .describe(describe)
                        .build();
                results.add(respDTO);

            } catch (Exception e) {
                log.error("导入CSV第{}行失败: {}", lineCount, line, e);
            }
        }

        log.info("从CSV导入短链接完成: 成功{}条", results.size());
        return results;
    }

    /**
     * CSV转义
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 解析CSV行
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());

        return fields.toArray(new String[0]);
    }
}
