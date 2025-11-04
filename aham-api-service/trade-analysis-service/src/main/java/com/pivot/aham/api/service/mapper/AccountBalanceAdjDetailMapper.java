package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface AccountBalanceAdjDetailMapper extends BaseMapper<AccountBalanceAdjDetail> {

    void batchInsertBalanceAdjDetail(List<AccountBalanceAdjDetail> accountBalanceAdjDetailList);
}