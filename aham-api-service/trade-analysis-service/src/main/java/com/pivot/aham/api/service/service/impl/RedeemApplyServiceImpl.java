package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.RedeemApplyMapper;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author addison
 */
@Service
public class RedeemApplyServiceImpl extends BaseServiceImpl<RedeemApplyPO, RedeemApplyMapper> implements RedeemApplyService {


    @Override
    public void updateByAccountId(RedeemApplyPO vaRedeemApplyPO) {
        mapper.updateByAccountId(vaRedeemApplyPO);
    }

    @Override
    public void updateByTmpOrderId(RedeemApplyPO vaRedeemApplyPO) {

        mapper.updateByTmpOrderId(vaRedeemApplyPO);
    }

    @Override
    public List<RedeemApplyPO> queryByApplyTime(RedeemApplyPO redeemApplyPO) {
        return mapper.queryByApplyTime(redeemApplyPO);
    }

    @Override
    public void updateRedeemApplyById(RedeemApplyPO redeemApplyPO) {
        mapper.updateRedeemApplyById(redeemApplyPO);
    }

    @Override
    public void insertRedeemApply(RedeemApplyPO redeemApplyPO) {
        mapper.insertRedeemApply(redeemApplyPO);
    }

    @Override
    public List<RedeemApplyPO> listRedeemApply(RedeemApplyPO redeemApplyParam) {
        return mapper.listRedeemApply(redeemApplyParam);
    }

    @Override
    public RedeemApplyPO queryByRedeemApplyId(Long redeemApplyId) {
        return mapper.queryByRedeemApplyId(redeemApplyId);
    }

}
