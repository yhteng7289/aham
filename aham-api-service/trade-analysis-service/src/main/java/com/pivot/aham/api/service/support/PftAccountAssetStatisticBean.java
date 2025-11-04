package com.pivot.aham.api.service.support;

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
public class PftAccountAssetStatisticBean {

    @TableField("product_code")
    private String productCode;
    @TableField("product_share")
    private BigDecimal productShare;
    @TableField("product_money")
    private BigDecimal productMoney;
    @TableField("product_asset_status")
    private ProductAssetStatusEnum productAssetStatus;

}
