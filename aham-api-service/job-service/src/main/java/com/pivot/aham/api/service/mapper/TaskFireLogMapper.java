package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.common.model.TaskFireLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TaskFireLogMapper extends BaseMapper<TaskFireLog> {
    List<Long> selectIdByMap(RowBounds rowBounds, @Param("cm") Map<String, Object> params);
}
