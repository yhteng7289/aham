package com.pivot.aham.api.web.web.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "UserStatementReqVo请求对象说明")
public class UserStatementReqVo extends BaseVo {

    @ApiModelProperty(value = "用户id", required = true)
    private String clientId;

}
