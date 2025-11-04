package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author addison
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_fund_nav",resultMap = "UserFundNavRes")
public class UserFundNavPO extends BaseModel {
    private String clientId;
    private BigDecimal fundNav;
    private Date navTime;
    private BigDecimal totalAsset;
    private Long accountId;
    private BigDecimal totalShare;
    private String goalId;

    //查询辅助
    private Date startNavTime;
    private Date endNavTime;

}
