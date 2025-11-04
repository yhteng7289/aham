package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户product资产
 *
 * @author addison
 * @since 2018年12月13日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_asset",resultMap = "UserAssetRes")
public class UserAssetPO extends BaseModel {
    private String productCode;
    private Long accountId;
    private String clientId;
    private BigDecimal share;
    private BigDecimal money;
    private Date assetTime;
    private String goalId;

    private Date startAssetTime;
    private Date endAssetTime;

}
