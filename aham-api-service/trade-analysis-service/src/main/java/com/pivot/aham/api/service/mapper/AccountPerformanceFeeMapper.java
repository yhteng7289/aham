package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountPerformanceFee;
import com.pivot.aham.common.core.base.BaseMapper;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
public interface AccountPerformanceFeeMapper extends BaseMapper<AccountPerformanceFee> {
    void saveAccountPerformanceFee(AccountPerformanceFee accountPerformanceFee);
    
    BigDecimal getSumAccPerformanceFee(AccountPerformanceFee accountPerformanceFee);
    
    BigDecimal getSumAccPerformanceFeeGst(AccountPerformanceFee accountPerformanceFee);
    
    void updateAccPerformanceFeeStatus(AccountPerformanceFee accountPerformanceFee);
    
    List <AccountPerformanceFee> listAccPerformanceFee(AccountPerformanceFee accountPerformanceFee);
}
