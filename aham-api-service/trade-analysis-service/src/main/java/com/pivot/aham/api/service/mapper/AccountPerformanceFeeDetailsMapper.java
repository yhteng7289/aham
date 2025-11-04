package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.AccountPerformanceFeeDetails;
import com.pivot.aham.common.core.base.BaseMapper;
import java.math.BigDecimal;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author WooiTatt
 * @since 2019年01月22日
 */
public interface AccountPerformanceFeeDetailsMapper extends BaseMapper<AccountPerformanceFeeDetails> {
    
    void saveAccountPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    AccountPerformanceFeeDetails getLastAccPerFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    List<AccountPerformanceFeeDetails> getListAccPerformanceFeeDetails(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
    BigDecimal getSumDeductedShare(AccountPerformanceFeeDetails accountPerformanceFeeDetails);
    
}
