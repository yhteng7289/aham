package com.pivot.aham.api.service.job.impl.rebalance;

import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.api.service.mapper.model.AccountBalanceRecord;
import com.pivot.aham.api.service.service.AccountBalanceAdjDetailService;
import com.pivot.aham.api.service.service.AccountBalanceRecordService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 方案详情
 *
 * @author addison
 * @since 2019年03月24日
 */
@Component
@Scope(value = "prototype")
@Slf4j
@Data
public class AdjustPlanSellBuilderSupport {

    private AccountBalanceRecord accountBalanceRecord;
    private List<AccountBalanceAdjDetail> accountBalanceAdjDetailList;
    private AccountBalanceHisRecord accountBalanceHisRecord;

    @Autowired
    private AccountBalanceRecordService accountBalanceRecordService;
    @Autowired
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;

    @Transactional(rollbackFor = Throwable.class)
    public void genAdj() {
        accountBalanceRecordService.updateOrInsert(accountBalanceRecord);
        for (AccountBalanceAdjDetail accountBalanceAdjDetail : accountBalanceAdjDetailList) {
            accountBalanceAdjDetail.setBalId(accountBalanceRecord.getId());
        }
        accountBalanceAdjDetailService.batchInsertBalanceAdjDetail(accountBalanceAdjDetailList);
        accountBalanceHisRecord.setBalId(accountBalanceRecord.getId());
    }

}
