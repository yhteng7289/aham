package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.common.core.base.BaseService;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoOrderTradeTypeEnum;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by luyang.li on 18/12/9.
 */
public interface SaxoAccountOrderService extends BaseService<SaxoAccountOrderPO> {

    void saveSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO);

    SaxoAccountOrderPO querySaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO);


    SaxoAccountOrderPO getRechargeSaxoAccountOrder(SaxoAccountOrderPO processingSgd, CurrencyEnum currency,
                                                   BigDecimal cashAmount, SaxoOrderTradeStatusEnum tradeStatus,
                                                   SaxoOrderTradeTypeEnum tradeType, SaxoOrderActionTypeEnum actionType, Long exchangeTotalOrderId);

    void saveBatch(List<SaxoAccountOrderPO> saxoAccountOrderPOAdds);

    void updateSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrder);

    List<SaxoAccountOrderPO> listSaxoAccountOrder(SaxoAccountOrderPO po);

    BigDecimal getClientGoalMoney(SaxoAccountOrderPO sgdRechargeParam);

}
