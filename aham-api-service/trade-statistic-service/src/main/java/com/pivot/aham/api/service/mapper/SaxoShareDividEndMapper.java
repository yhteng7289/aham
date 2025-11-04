package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoShareDividEndPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoShareDividEndMapper extends BaseMapper<SaxoShareDividEndPO>{

    Integer getCountByCond(SaxoShareDividEndPO saxoShareDividEndPO);
    int batchInsert(@Param("lists") List<SaxoShareDividEndPO> lists);

    List<SaxoShareDividEndPO>getListByCond(SaxoShareDividEndPO saxoShareDividEndPO);
    int batchUpdate(@Param("updateLists") List<SaxoShareDividEndPO> lists);


}
