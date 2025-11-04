package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AhamOrderConfirmationPO;
import com.pivot.aham.api.service.mapper.model.SaxoOrderActivityPO;
import com.pivot.aham.common.core.base.BaseMapper;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface AhamOrderConfirmationMapper extends BaseMapper {

    void save(AhamOrderConfirmationPO ahamOrderConfirmationPO);

    SaxoOrderActivityPO get();

}
