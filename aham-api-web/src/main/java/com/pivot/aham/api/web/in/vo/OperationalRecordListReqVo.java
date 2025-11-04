/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@Data
@ApiModel("OperationalRecordReqVo-请求对象说明")
public class OperationalRecordListReqVo {

    @ApiModelProperty(value = "开始日期", required = false)
    private Date startCreateTime;
    @ApiModelProperty(value = "结束日期", required = false)
    private Date endCreateTime;
}
