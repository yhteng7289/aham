package com.pivot.aham.api.service.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pivot.aham.api.service.mapper.model.SaxoCashTransactionsPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoCashTransactionsMapper extends BaseMapper<SaxoCashTransactionsPO> {
    int batchInsert(@Param("lists") List<SaxoCashTransactionsPO> lists);
}
