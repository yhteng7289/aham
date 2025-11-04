/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.OperationalRecordMapper;
import com.pivot.aham.api.service.mapper.model.OperationalRecordPO;
import com.pivot.aham.api.service.service.OperationalRecordService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

/**
 *
 * @author HP
 */
@Service
@Slf4j
public class OperationalRecordServiceImpl extends BaseServiceImpl<OperationalRecordPO, OperationalRecordMapper> implements OperationalRecordService {

    @Override
    public void insertOperationalRecord(OperationalRecordPO operationalRecordPO) {
        mapper.insertOperationalRecord(operationalRecordPO);
    }

    @Override
    public List<OperationalRecordPO> getOperationRecordByDate(RowBounds rowBounds, Date startTime, Date endTime) {
        return mapper.getOperationRecordByDate(rowBounds, startTime, endTime);
    }

}
