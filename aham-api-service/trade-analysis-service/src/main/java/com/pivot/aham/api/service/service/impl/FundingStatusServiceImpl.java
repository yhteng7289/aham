package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.FundingStatusMapper;
import com.pivot.aham.api.service.mapper.model.FundingStatusPO;
import com.pivot.aham.api.service.service.FundingStatusService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

/**
 *
 * @author bjoon
 */
@Service
@Slf4j
public class FundingStatusServiceImpl extends BaseServiceImpl<FundingStatusPO, FundingStatusMapper> implements FundingStatusService {
    @Override
    public List<FundingStatusPO> getFundingStatusPageList(RowBounds rowBound, FundingStatusPO fundingStatusPO, String clientId, Date startCreateTime, Date endCreateTime) {
        return mapper.getFundingStatusPageList(rowBound, fundingStatusPO, clientId, startCreateTime, endCreateTime);
    }
}
