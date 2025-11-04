package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;


public interface RedeemApplyService extends BaseService<RedeemApplyPO> {
    void updateByAccountId(RedeemApplyPO vaRedeemApplyPO);
    void updateByTmpOrderId(RedeemApplyPO vaRedeemApplyPO);
    List<RedeemApplyPO> queryByApplyTime(RedeemApplyPO redeemApplyPO);

    void updateRedeemApplyById(RedeemApplyPO redeemApplyPO);

    void insertRedeemApply(RedeemApplyPO redeemApplyPO);

    List<RedeemApplyPO> listRedeemApply(RedeemApplyPO redeemApplyParam);

    RedeemApplyPO queryByRedeemApplyId(Long redeemApplyId);

}
