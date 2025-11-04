package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.ProductAssetStatusEnum;
import com.pivot.aham.common.enums.analysis.AssetSourceEnum;
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
@TableName(value = "t_account_asset", resultMap = "AccountAssetRes")
public class AccountAssetPO extends BaseModel {

    private String productCode;
    private Long accountId;
    private BigDecimal confirmShare;
    private BigDecimal confirmMoney;
    private BigDecimal applyMoney;
    private ProductAssetStatusEnum productAssetStatus;
    private Long rechargeOrderNo;
    private Date applyTime;
    private Date confirmTime;
    private Long totalTmpOrderId;
    private Long tmpOrderId;
    /**
     * 分红幂等关联单号
     */
    private String dividendOrderId;
    /**
     * 资产来源
     */
    private AssetSourceEnum assetSource;

    //查询辅助
    private Date startApplyTime;
    private Date endApplyTime;
    private Date createEndTime;

    public AccountAssetPO() {
    }

    public AccountAssetPO(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

}
