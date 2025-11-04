package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.SaxoAccountOrderMapper;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author addison
 */
@Service
public class SaxoAccountOrderServiceImpl extends BaseServiceImpl<SaxoAccountOrderPO, SaxoAccountOrderMapper> implements SaxoAccountOrderService {

    @Override
    public void saveSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO) {
        mapper.saveSaxoAccountOrder(saxoAccountOrderPO);
    }

    @Override
    public SaxoAccountOrderPO querySaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO) {
        return mapper.querySaxoAccountOrder(saxoAccountOrderPO);
    }

    @Override
    public SaxoAccountOrderPO getRechargeSaxoAccountOrder(SaxoAccountOrderPO order,
                                                          CurrencyEnum currency,
                                                          BigDecimal money,
                                                          SaxoOrderTradeStatusEnum tradeStatus,
                                                          SaxoOrderTradeTypeEnum tradeType,
                                                          SaxoOrderActionTypeEnum actionType,
                                                          Long exchangeTotalOrderId) {
        SaxoAccountOrderPO saxoAccountOrderPO = new SaxoAccountOrderPO();
        saxoAccountOrderPO.setAccountId(order.getAccountId());
        saxoAccountOrderPO.setClientId(order.getClientId());
        saxoAccountOrderPO.setExchangeOrderNo(order.getExchangeOrderNo());
        saxoAccountOrderPO.setId(Sequence.next());
        saxoAccountOrderPO.setCurrency(currency);
        saxoAccountOrderPO.setOrderStatus(tradeStatus);
        saxoAccountOrderPO.setOperatorType(tradeType);
        saxoAccountOrderPO.setActionType(actionType);
        saxoAccountOrderPO.setTradeTime(DateUtils.now());
        saxoAccountOrderPO.setCashAmount(money);
        saxoAccountOrderPO.setBankOrderNo(order.getBankOrderNo());
        saxoAccountOrderPO.setUpdateTime(DateUtils.now());
        saxoAccountOrderPO.setCreateTime(DateUtils.now());
        saxoAccountOrderPO.setGoalId(order.getGoalId());
        saxoAccountOrderPO.setRedeemApplyId(0L);
        saxoAccountOrderPO.setExchangeTotalOrderId(exchangeTotalOrderId);
        return saxoAccountOrderPO;
    }

    @Override
    public void saveBatch(List<SaxoAccountOrderPO> saxoAccountOrderPOAdds) {
        mapper.saveBatch(saxoAccountOrderPOAdds);
    }

    @Override
    public void updateSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrder) {
        mapper.updateSaxoAccountOrder(saxoAccountOrder);
    }

    @Override
    public List<SaxoAccountOrderPO> listSaxoAccountOrder(SaxoAccountOrderPO po) {
        return mapper.listSaxoAccountOrder(po);
    }

    @Override
    public BigDecimal getClientGoalMoney(SaxoAccountOrderPO sgdRechargeParam) {
        List<SaxoAccountOrderPO> rechargeSaxoAccountOrderPOS = listSaxoAccountOrder(sgdRechargeParam);
        BigDecimal rechargeMoney = BigDecimal.ZERO;
        for (SaxoAccountOrderPO saxoAccountOrderPO : rechargeSaxoAccountOrderPOS) {
            rechargeMoney = rechargeMoney.add(saxoAccountOrderPO.getCashAmount()).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return rechargeMoney;
    }

}
