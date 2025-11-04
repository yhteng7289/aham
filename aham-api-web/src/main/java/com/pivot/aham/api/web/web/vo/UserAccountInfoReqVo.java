package com.pivot.aham.api.web.web.vo;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月01日
 */
@Data
@Accessors(chain=true)
@ApiModel(value = "用户注册信息同步")
public class UserAccountInfoReqVo extends BaseVo {
    @ApiModelProperty(value = "银行转账使用的code:referenceCode", required = true)
    private String referenceCode;
    @ApiModelProperty(value = "投资目标:goalId", required = true)
    private String goalId;
    @ApiModelProperty(value = "方案标识:portfolioId", required = true)
    private String portfolioId;
    @ApiModelProperty(value = "风险等级:riskLevel", required = true)
    private Integer risk;
    @ApiModelProperty(value = "最后一次调仓时间:lastAdjTime", required = true)
    private Date lastAdjTime;
    @ApiModelProperty(value = "用户年龄:ageLevel", required = true)
    private Integer age;
    @ApiModelProperty(value = "投资规模:investType: pooling，tailer", required = true)
    private Integer investType;
}
