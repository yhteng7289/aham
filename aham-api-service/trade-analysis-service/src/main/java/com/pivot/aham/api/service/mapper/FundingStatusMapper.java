package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.FundingStatusPO;
import com.pivot.aham.common.core.base.BaseMapper;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author bjoon
 */
public interface FundingStatusMapper extends BaseMapper<FundingStatusPO>{
    List<FundingStatusPO> getFundingStatusPageList(RowBounds rowBound, FundingStatusPO fundingStatusPO,
            @Param("clientId") String clientId,
            @Param("startCreateTime") Date startCreateTime, @Param("endCreateTime") Date endCreateTime);
}
