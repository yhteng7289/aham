package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoShareOpenPositionsPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoShareOpenPositionsMapper extends BaseMapper<SaxoShareOpenPositionsPO> {
    int batchInsert(@Param("lists") List<SaxoShareOpenPositionsPO> lists);
    List<SaxoShareOpenPositionsPO>getListByCond(SaxoShareOpenPositionsPO saxoShareOpenPositionsPO);
}
