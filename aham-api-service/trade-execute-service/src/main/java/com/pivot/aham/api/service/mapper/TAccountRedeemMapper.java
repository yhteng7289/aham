package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.TAccountRedeemPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface TAccountRedeemMapper extends BaseMapper {

    List<TAccountRedeemPO> getRedeemListByTime(TAccountRedeemPO accountRedeemPO);
    void updateByAccountId(TAccountRedeemPO accountRedeemPO);
    void updateByTotalTmpOrderId(TAccountRedeemPO accountRedeemPO);

    List<TAccountRedeemPO> listAccountRedeem(TAccountRedeemPO accountRedeem);

    List<TAccountRedeemPO> listAccountRedeemByCond(TAccountRedeemPO accountRedeem);
	
    void insertAccountRedeem(TAccountRedeemPO accountRedeemPO);

    void updateAccountRedeemById(TAccountRedeemPO accountRedeemPO);

    void updateAccountRedeemToTncf(@Param("accountRedeemIds") List<Long> accountRedeemIds,
                                   @Param("tncfStatus") TncfStatusEnum tncfStatus);
    
    List<TAccountRedeemPO> getRedeemListByTimeOrderPF(TAccountRedeemPO accountRedeemPO);
    
}
