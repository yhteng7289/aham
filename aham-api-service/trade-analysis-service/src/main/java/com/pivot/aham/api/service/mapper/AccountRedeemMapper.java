package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountRedeemMapper extends BaseMapper<AccountRedeemPO> {
    List<AccountRedeemPO> getRedeemListByTime(AccountRedeemPO accountRedeemPO);
    void updateByAccountId(AccountRedeemPO accountRedeemPO);
    void updateByTotalTmpOrderId(AccountRedeemPO accountRedeemPO);

    List<AccountRedeemPO> listAccountRedeem(AccountRedeemPO accountRedeem);

    void insertAccountRedeem(AccountRedeemPO accountRedeemPO);

    void updateAccountRedeemById(AccountRedeemPO accountRedeemPO);

    void updateAccountRedeemToTncf(@Param("accountRedeemIds") List<Long> accountRedeemIds,
                                   @Param("tncfStatus") TncfStatusEnum tncfStatus);
    
    List<AccountRedeemPO> getRedeemListByTimeOrderPF(AccountRedeemPO accountRedeemPO); //Added By WooiTatt
    
    BigDecimal getSumRedeemConfirmAmount();
    
    BigDecimal getSumRedeemConfirmAmtByGoalClient(AccountRedeemPO accountRedeemPO);

}