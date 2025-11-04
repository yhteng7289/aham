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

/**
 *
 * @author ASUS
 */
@Data
@ApiModel("SaxoOpenPositionReqVo-请求对象说明")
public class SaxoOpenPositionReqVo {
    
    @ApiModelProperty(value = "页码", required = true)
    private Integer pageNo;
    @ApiModelProperty(value = "页大小", required = true)
    private Integer pageSize;
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startCreateTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Date endCreateTime;
    
}
