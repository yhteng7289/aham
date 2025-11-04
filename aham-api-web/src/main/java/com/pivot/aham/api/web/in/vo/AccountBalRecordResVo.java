package com.pivot.aham.api.web.in.vo;

import com.pivot.aham.common.enums.analysis.BalStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("AccountBalRecordResVo-请求对象说明")
public class AccountBalRecordResVo {
    @ApiModelProperty(value = "accountId",required = true)
    private String accountId;
    @ApiModelProperty(value = "策略id",required = true)
    private String portfolioId;
    @ApiModelProperty(value = "调仓时间",required = true)
    private Date balTime;
    @ApiModelProperty(value = "调仓记录id",required = true)
    private String balId;
    @ApiModelProperty(value = "调仓状态",required = true)
    private BalStatusEnum balStatus;
}
