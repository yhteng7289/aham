/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.admin.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
@ApiModel("SysOperationActivityReqVo-请求对象说明")

public class SysOperationActivityReqVo {

    @ApiModelProperty(value = "Start time", required = false)
    private Date startTime;
    @ApiModelProperty(value = "End time", required = false)
    private Date endTime;
    @ApiModelProperty(value = "Page No", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "Page Size", required = true)
    private Integer pageSize;

}
