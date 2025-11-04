package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoShareTradesExecutedPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoShareTradesExecutedMapper extends BaseMapper<SaxoShareTradesExecutedPO>{
    List<SaxoShareTradesExecutedPO> getDataByCond(SaxoShareTradesExecutedPO saxoShareTradesExecutedPO);
    int batchInsert(@Param("lists") List<SaxoShareTradesExecutedPO> lists);

}
