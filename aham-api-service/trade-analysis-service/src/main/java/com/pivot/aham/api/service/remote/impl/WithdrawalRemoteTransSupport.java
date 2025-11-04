package com.pivot.aham.api.service.remote.impl;

import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.api.service.service.RedeemApplyService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月13日
 */
@Component
@Scope(value = "prototype")
@Data
@Transactional(rollbackFor = Throwable.class)
public class WithdrawalRemoteTransSupport {
    private AccountRedeemPO accountRedeemPO;
    private RedeemApplyPO redeemApplyPO;

    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Autowired
    private AccountRedeemService accountRedeemService;

    public void saveRedeemInfoForFromVirtalAccount(){
        bankVARedeemService.updateOrInsert(redeemApplyPO);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveRedeemInfoForFromGoal(){
        RedeemApplyPO redeemApply =bankVARedeemService.updateOrInsert(redeemApplyPO);
        accountRedeemPO.setRedeemApplyId(redeemApply.getId());
        accountRedeemService.updateOrInsert(accountRedeemPO);

    }

}
