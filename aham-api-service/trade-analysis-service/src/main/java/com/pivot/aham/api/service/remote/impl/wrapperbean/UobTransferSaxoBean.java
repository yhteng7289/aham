package com.pivot.aham.api.service.remote.impl.wrapperbean;

import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors(chain = true)
public class UobTransferSaxoBean {
    private BigDecimal applyMoney;
    private List<SaxoAccountOrderPO> confirmSaxoAccountOrders;
}
