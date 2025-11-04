package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.UserBatchNavEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author WooiTatt
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_batch_nav", resultMap = "UserBatchNavRes")
public class UserBatchNavPO extends BaseModel {

    private String clientId;
    private String accountId;
    private String goalId;
    private BigDecimal currFundNav;
    private BigDecimal currTotalShare;
    private String batchNo;
    private UserBatchNavEnum status;
    private Date createTime;
    private Date updateTime;

}
