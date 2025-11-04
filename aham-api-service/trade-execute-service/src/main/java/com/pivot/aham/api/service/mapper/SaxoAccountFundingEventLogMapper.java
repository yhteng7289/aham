package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SaxoAccountFundingEventLogPO;
import com.pivot.aham.common.core.base.BaseMapper;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface SaxoAccountFundingEventLogMapper extends BaseMapper {

    SaxoAccountFundingEventLogPO getLast();
    void save(SaxoAccountFundingEventLogPO saxoAccountFundingEventPO);
}
