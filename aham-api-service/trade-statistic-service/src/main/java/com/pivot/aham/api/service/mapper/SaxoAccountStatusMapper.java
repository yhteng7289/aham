package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoAccountStatusPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoAccountStatusMapper extends BaseMapper<SaxoAccountStatusPO> {
    int batchInsert(@Param("lists") List<SaxoAccountStatusPO> lists);

    SaxoAccountStatusPO getDataByCond(SaxoAccountStatusPO saxoAccountStatusPO);

}
