package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.UsageMetrics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用量统计表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface UsageMetricsMapper extends BaseMapper<UsageMetrics> {

}
