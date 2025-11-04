package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import com.pivot.aham.api.service.job.interevent.CashDividendEvent;
import com.pivot.aham.api.service.mapper.model.AccountAssetPO;
import com.pivot.aham.api.service.mapper.model.AccountDividendPO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountDividendService;
import com.pivot.aham.api.service.service.DividendSupportService;
import com.pivot.aham.api.service.service.UserDividendService;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.enums.analysis.CaEventTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Service
@Slf4j
public class DividendSupportServiceImpl implements DividendSupportService {

    @Resource
    private AccountDividendService accountDividendService;
    @Resource
    private EventBus accountStaticsBus;
    @Resource
    private UserDividendService userDividendService;
    @Resource
    private AccountAssetService accountAssetService;

    @Override
    public void handelAccountDividend(AccountDividendPO accountDividendPO, AccountEtfSharesPO accountEtfSharesPO) {

        //处理账户的分红 1.幂等检查
        AccountDividendPO accountDividendParam = new AccountDividendPO();
        accountDividendParam.setDividendOrderId(accountDividendPO.getDividendOrderId());
        accountDividendParam.setAccountId(accountEtfSharesPO.getAccountId());
        accountDividendParam.setProductCode(accountDividendPO.getProductCode());
        accountDividendParam.setExDate(accountDividendPO.getExDate());
        AccountDividendPO alreadyAccountDividend = accountDividendService.queryAccountDividend(accountDividendParam);
        if (null == alreadyAccountDividend) {
            accountDividendPO.setId(Sequence.next());
            accountDividendService.insert(accountDividendPO);
        } else {
            accountDividendPO.setId(alreadyAccountDividend.getId());
            accountDividendService.updateAccountDividend(accountDividendPO);
        }

    }

    @Override
    @Transactional
    public void handelUserDividend(List<UserDividendPO> userDividendPOS,
                                   AccountAssetPO accountAssetPO,
                                   AccountDividendPO accountDividendPO,
                                   AccountEtfSharesPO accountEtfSharesPO) {

        accountDividendService.updateAccountDividend(accountDividendPO);
        //处理用户的分红
        for (UserDividendPO userDividendPO : userDividendPOS) {
            //幂等检查
            UserDividendPO userDividendParam = new UserDividendPO();
            userDividendParam.setDividendOrderId(accountDividendPO.getDividendOrderId());
            userDividendParam.setAccountId(accountDividendPO.getAccountId());
            userDividendParam.setGoalId(userDividendPO.getGoalId());
            userDividendParam.setClientId(userDividendPO.getClientId());
            userDividendParam.setDividendDate(userDividendPO.getDividendDate());
            UserDividendPO alreadyUserDividend = userDividendService.queryUserDividend(userDividendParam);
            if (null == alreadyUserDividend) {
                userDividendPO.setId(Sequence.next());
                userDividendService.insert(userDividendPO);
            } else {
                userDividendPO.setId(alreadyUserDividend.getId());
                userDividendService.update(userDividendPO);
            }
        }

        if (null != accountAssetPO) {
            //处理资产
            AccountAssetPO accountAssetParam = new AccountAssetPO();
            accountAssetParam.setDividendOrderId(accountDividendPO.getDividendOrderId());
            accountAssetParam.setAccountId(accountDividendPO.getAccountId());
            accountAssetParam.setProductCode(accountDividendPO.getProductCode());
            AccountAssetPO alreadyAccountAsset = accountAssetService.queryAccountAsset(accountAssetParam);
            if (null == alreadyAccountAsset) {
                accountAssetPO.setId(Sequence.next());
                accountAssetService.saveAccountAsset(accountAssetPO);
            } else {
                accountAssetPO.setId(alreadyAccountAsset.getId());
                accountAssetService.update(accountAssetPO);
            }

            //发送分红事件
            if (accountDividendPO.getCaEventTypeID().equals(CaEventTypeEnum.CASH.getValue())) {
                CashDividendEvent cashDividendEvent = new CashDividendEvent();
                cashDividendEvent.setAccountId(accountDividendPO.getAccountId());
                cashDividendEvent.setCashDividend(accountDividendPO.getDividendAmount());
                accountStaticsBus.post(cashDividendEvent);
            }
        } else {
            log.info("分红记账，改账户下的用户全部赎回，没有分配给用户的分红,accountEtfSharesPO:{}", JSON.toJSONString(accountEtfSharesPO));
        }

    }

}
