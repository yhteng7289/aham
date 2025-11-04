package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.ReduceStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_normal_fee",resultMap = "AccountNormalFeeRes")
public class AccountNormalFee extends BaseModel{
    private Long accountId;
    private String clientId;
    private BigDecimal mgtFee;
    private BigDecimal custFee;
    private BigDecimal mgtGst;
    private ReduceStatusEnum reduceStatus;

}
