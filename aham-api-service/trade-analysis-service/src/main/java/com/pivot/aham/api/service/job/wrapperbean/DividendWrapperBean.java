package com.pivot.aham.api.service.job.wrapperbean;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors
public class DividendWrapperBean {
    private List<UserDividendPO> userDividendPOList = Lists.newArrayList();
    private BigDecimal subtractMoney = BigDecimal.ZERO;

}
