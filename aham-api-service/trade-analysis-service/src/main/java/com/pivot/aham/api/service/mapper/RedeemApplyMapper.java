package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RedeemApplyMapper extends BaseMapper<RedeemApplyPO> {
    void updateByAccountId(RedeemApplyPO vaRedeemApplyPO);
    void updateByTmpOrderId(RedeemApplyPO vaRedeemApplyPO);
    List<RedeemApplyPO> queryByApplyTime(RedeemApplyPO redeemApplyPO);

    void updateRedeemApplyById(RedeemApplyPO redeemApplyPO);

    void insertRedeemApply(RedeemApplyPO redeemApplyPO);

    List<RedeemApplyPO> listRedeemApply(RedeemApplyPO redeemApplyParam);

    RedeemApplyPO queryByRedeemApplyId(@Param("redeemApplyId") Long redeemApplyId);

}