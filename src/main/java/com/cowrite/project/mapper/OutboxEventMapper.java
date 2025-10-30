package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.OutboxEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * Outbox事件表 Mapper 接口
 *
 * @author heathcetide
 */
@Mapper
public interface OutboxEventMapper extends BaseMapper<OutboxEvent> {

}
