package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
@ApiModel("UobTransferReqVo-请求对象说明")
public class UobTransferReqVo extends BaseVo {

    @ApiModelProperty(value = "月份", required = false)
    private String month;

}
