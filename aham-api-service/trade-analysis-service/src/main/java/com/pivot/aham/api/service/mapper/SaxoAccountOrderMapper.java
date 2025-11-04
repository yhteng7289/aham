package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Repository
public interface SaxoAccountOrderMapper extends BaseMapper<SaxoAccountOrderPO> {

    void saveSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO);

    void updateSaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO);

    SaxoAccountOrderPO listSaxoAccountOrders(SaxoAccountOrderPO saxoAccountOrderPO);

    SaxoAccountOrderPO querySaxoAccountOrder(SaxoAccountOrderPO saxoAccountOrderPO);

    void saveBatch(List<SaxoAccountOrderPO> saxoAccountOrderPOAdds);

    List<SaxoAccountOrderPO> listSaxoAccountOrder(SaxoAccountOrderPO po);

}
