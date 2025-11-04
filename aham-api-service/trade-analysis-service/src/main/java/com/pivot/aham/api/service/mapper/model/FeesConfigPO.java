package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;
import com.pivot.aham.common.enums.analysis.ReduceStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_fees_config",resultMap = "FeesConfigRes")
public class FeesConfigPO extends BaseModel{
    private Long id;
    private BigDecimal rateCharge;
    private FeeTypeEnum feeType;
    private Date startDate;
    private Date endDate;
    private String activeStatus;
    private Date createDate;
    private Date updateDate;

}
