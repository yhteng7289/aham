package com.pivot.aham.api.service.job.impl.rebalance;

import com.pivot.aham.api.service.mapper.model.AccountBalanceAdjDetail;
import com.pivot.aham.api.service.service.AccountBalanceAdjDetailService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 调仓买单事务支持
 *
 * @author addison
 * @since 2019年03月24日
 */
@Component
@Scope(value = "prototype")
@Slf4j
@Data
public class AdjustPlanBuyBuilderSupport {

    private List<AccountBalanceAdjDetail> accountBalanceAdjDetailList;

    @Autowired
    private AccountBalanceAdjDetailService accountBalanceAdjDetailService;

    @Transactional(rollbackFor = Throwable.class)
    public void genBuyAdj(){
        accountBalanceAdjDetailService.batchInsertBalanceAdjDetail(accountBalanceAdjDetailList);
    }


}
