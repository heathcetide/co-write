package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.BillingInvoiceItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账单明细表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface BillingInvoiceItemMapper extends BaseMapper<BillingInvoiceItem> {

}
