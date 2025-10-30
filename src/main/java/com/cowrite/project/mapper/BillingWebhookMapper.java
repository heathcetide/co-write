package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingWebhook;
import org.apache.ibatis.annotations.Mapper;

/**
 * Webhook配置表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingWebhookMapper extends BaseMapper<BillingWebhook> {

}
