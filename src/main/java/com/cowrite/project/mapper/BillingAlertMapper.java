package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingAlert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 阈值预警配置表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingAlertMapper extends BaseMapper<BillingAlert> {

}
