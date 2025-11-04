package com.pivot.aham.api.service.support;/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月18日
 */

import com.baomidou.mybatisplus.annotations.TableField;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月18日
 */
@Data
public class AccountAssetStatisticBean {
    @TableField("product_code")
    private String productCode;
    @TableField("account_id")
    private Long accountId;
    @TableField("product_share")
    private BigDecimal productShare;
    @TableField("product_money")
    private BigDecimal productMoney;
    @TableField("product_asset_status")
    private ProductAssetStatusEnum productAssetStatus;




}
