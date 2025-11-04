package com.pivot.aham.api.web.admin.vo.res;

import com.pivot.aham.common.core.base.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
@ApiModel("UobTransferResVo-请求对象说明")
public class UobTransferResVo extends BaseModel {
    //private List<UobExecutionRecord> uobExecutionRecords;
    private Boolean reportSendStatus;
    private BigDecimal totalCost;
}
