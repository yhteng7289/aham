package com.pivot.aham.api.service.rebalance;

import com.pivot.aham.api.server.dto.ModelRecommendResDTO;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountBalanceHisRecord;
import com.pivot.aham.api.service.service.AccountBalanceHisRecordService;
import com.pivot.aham.common.core.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountRebalanceTest {


    @Autowired
    private AccountBalanceHisRecordService accountBalanceHisRecordService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;


    @Test
    public void updateAccountHis(){
        //根据模型标识获取当日目标模型
        ModelRecommendResDTO modelRecommendResDTO = modelServiceRemoteService.getValidRecommendByPortfolioId("P2R5A4");

        AccountBalanceHisRecord hisRecord = new AccountBalanceHisRecord();
        hisRecord.setBalId(1L);
        hisRecord.setAccountId(123L);
        hisRecord.setPortfolioScore(new BigDecimal(11L));
        hisRecord.setLastBalTime(DateUtils.now());
        hisRecord.setLastProductWeight("123");
        accountBalanceHisRecordService.updateByAccountId(hisRecord);
    }
}
