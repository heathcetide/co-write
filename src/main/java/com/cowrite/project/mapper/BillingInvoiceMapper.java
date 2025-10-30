package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingInvoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingInvoiceMapper extends BaseMapper<BillingInvoice> {

}
