package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingWebhookLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Webhook发送记录表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingWebhookLogMapper extends BaseMapper<BillingWebhookLog> {

}
