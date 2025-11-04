package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountDailyRecord;
import com.pivot.aham.common.core.base.BaseMapper;

import java.util.List;

public interface BankVirtualAccountDailyRecordMapper extends BaseMapper<BankVirtualAccountDailyRecord> {
    BankVirtualAccountDailyRecord selectByStaticDate(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord);

    List<BankVirtualAccountDailyRecord> queryByTime(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord);
}
