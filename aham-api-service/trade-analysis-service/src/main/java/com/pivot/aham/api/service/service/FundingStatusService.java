package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.FundingStatusPO;
import com.pivot.aham.common.core.base.BaseService;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author bjoon
 */
public interface FundingStatusService extends BaseService<FundingStatusPO>{
    List<FundingStatusPO> getFundingStatusPageList(RowBounds rowBound, FundingStatusPO fundingStatusPO, String clientId, Date startCreateTime, Date endCreateTime);
}
