package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccountDailyRecord;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface BankVirtualAccountDailyRecordService extends BaseService<BankVirtualAccountDailyRecord> {
    BankVirtualAccountDailyRecord selectByStaticDate(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord);

    List<BankVirtualAccountDailyRecord> queryByTime(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord);

}
