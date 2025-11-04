/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.OperationalRecordPO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author HP
 */
public interface OperationalRecordMapper extends BaseMapper<OperationalRecordPO> {

    void insertOperationalRecord(OperationalRecordPO operationalRecordPO);

    List<OperationalRecordPO> getOperationRecordByDate(RowBounds rowBound, Date startTime, Date endTime);

}
