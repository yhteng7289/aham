package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoBookkeepingCashPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoBookkeepingCashMapper extends   BaseMapper<SaxoBookkeepingCashPO>{
    Integer getDataByCond(SaxoBookkeepingCashPO saxoBookkeepingCashPO);
    int batchInsert(@Param("lists") List<SaxoBookkeepingCashPO> lists);
}
