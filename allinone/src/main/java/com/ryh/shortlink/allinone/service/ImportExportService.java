package com.ryh.shortlink.allinone.service;

import com.ryh.shortlink.allinone.dto.resp.ShortLinkBaseInfoRespDTO;

import java.util.List;

/**
 * 导入导出服务接口
 */
public interface ImportExportService {

    /**
     * 导出短链接为CSV格式
     */
    String exportToCsv(String gid);

    /**
     * 从CSV导入短链接
     */
    List<ShortLinkBaseInfoRespDTO> importFromCsv(String gid, String csvContent);
}
