package com.cowrite.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cowrite.project.model.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
