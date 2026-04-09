package com.ryh.shortlink.allinone.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分组唯一标识实体
 */
@Data
@TableName("t_group_unique")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupUniqueDO {

    /**
     * 分组标识
     */
    private String gid;
}
