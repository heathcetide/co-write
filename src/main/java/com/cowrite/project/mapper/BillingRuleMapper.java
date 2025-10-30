package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计费规则表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingRuleMapper extends BaseMapper<BillingRule> {

}
