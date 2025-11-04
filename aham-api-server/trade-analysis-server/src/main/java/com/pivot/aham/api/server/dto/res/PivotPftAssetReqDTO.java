package com.pivot.aham.api.server.dto.res;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.analysis.PftAssetOperateTypeEnum;
import com.pivot.aham.common.enums.analysis.PftAssetSourceEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PivotPftAssetReqDTO extends BaseDTO {

    /**
     * 产品code
     */
    @NotBlank(message = "productCode不能为空")
    private String productCode;
    /**
     * 确认份额
     */
    private BigDecimal confirmShare;
    /**
     * 确认金额
     */
    private BigDecimal confirmMoney;
    /**
     * 出入金类型
     */
    private PftAssetOperateTypeEnum pftAssetOperateType;
    /**
     * 执行订单id
     */
    @NotNull(message = "executeOrderNo不能为空")
    private Long executeOrderNo;
    /**
     * 操作来源
     */
    private PftAssetSourceEnum pftAssetSource;

    /**
     * 手续费
     */
    private BigDecimal costFee;

    /**
     *
     */
    private Date executeTime;

//    private Long dataVersion;
}
