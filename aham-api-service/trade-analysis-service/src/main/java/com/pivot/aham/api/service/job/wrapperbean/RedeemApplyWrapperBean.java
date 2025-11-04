package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors
public class RedeemApplyWrapperBean extends RedeemApplyPO{
    private BigDecimal precent;


}
