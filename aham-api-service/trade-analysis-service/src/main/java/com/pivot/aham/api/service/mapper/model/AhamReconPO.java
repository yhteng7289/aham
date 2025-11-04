package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.AhamReconResultEnum;
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
@TableName(value = "t_aham_recon", resultMap = "AhamReconRes")
public class AhamReconPO extends BaseModel {
	private BigDecimal dasUnit;
	private BigDecimal inputUnit;
	private BigDecimal diffUnit;
	private AhamReconResultEnum reconResult;
	private Date dasTime;
	
	private Date startCreateTime;
	private Date endCreateTime;
        private String productCode;
}
