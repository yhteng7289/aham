package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.BankVirtualAccountDailyRecordMapper;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountDailyRecord;
import com.pivot.aham.api.service.service.BankVirtualAccountDailyRecordService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BankVirtualAccountDailyRecordServiceImpl extends BaseServiceImpl<BankVirtualAccountDailyRecord, BankVirtualAccountDailyRecordMapper> implements BankVirtualAccountDailyRecordService {

    @Override
    public BankVirtualAccountDailyRecord selectByStaticDate(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord) {
        return mapper.selectByStaticDate(bankVirtualAccountDailyRecord);
    }

    @Override
    public List<BankVirtualAccountDailyRecord> queryByTime(BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord) {
        return mapper.queryByTime(bankVirtualAccountDailyRecord);
    }
}
