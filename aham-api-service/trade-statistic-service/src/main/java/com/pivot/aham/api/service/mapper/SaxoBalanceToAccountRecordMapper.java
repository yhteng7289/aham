package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.*;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SaxoBalanceToAccountRecordMapper extends BaseMapper{
    Integer insertBalCash(SaxoBalCashPO saxoBalCashPO);
    Integer insertBalOfAccNotice(SaxoBalOfAccNoticePO balOfAccNoticePO);
    Integer insertBalHoldMoney(SaxoBalHoldMoneyPO saxoBalHoldMoneyPO);
    Integer batchInsertTradeOrder(@Param("lists")List<SaxoBalTradeOrderPO> lists);
    Integer batchInsertETFHoldMoney(@Param("lists")List<SaxoBalETFHoldMoneyPO>lists);
    Integer batchInsertBalOfAccNotice(@Param("lists")List<SaxoBalOfAccNoticePO>lists);

    int deleteBalCash(SaxoBalCashPO saxoBalCashPO);
    int deleteBalHoldMoney(SaxoBalHoldMoneyPO saxoBalHoldMoneyPO);
    int deleteBalOfAccNotice(SaxoBalOfAccNoticePO balOfAccNoticePO);
    int deleteBalTradeOrder(SaxoBalTradeOrderPO saxoBalTradeOrderPO);
    int deleteBalETFHoldMoney(SaxoBalETFHoldMoneyPO saxoBalETFHoldMoneyPO);
}
