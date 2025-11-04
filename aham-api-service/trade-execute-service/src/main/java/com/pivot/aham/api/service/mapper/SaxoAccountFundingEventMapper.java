package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.SaxoAccountFundingEventPO;
import com.pivot.aham.common.core.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by hao.tong on 2018/12/24.
 */
public interface SaxoAccountFundingEventMapper extends BaseMapper {

    SaxoAccountFundingEventPO getBySequence(@Param("sequenceId") String sequenceId);
    void save(SaxoAccountFundingEventPO saxoAccountFundingEventPO);
    SaxoAccountFundingEventPO getLast();

    List<SaxoAccountFundingEventPO> getUnConfirm(@Param("valueDate") String valueDate);
    void confirm(@Param("id") Long id);
}
