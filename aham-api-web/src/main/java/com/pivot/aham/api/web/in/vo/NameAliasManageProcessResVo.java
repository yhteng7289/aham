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
@ApiModel("NameAliasManageProcessReqVo-请求对象说明")
public class NameAliasManageProcessResVo {

    @ApiModelProperty(value = "Status", required = true)
    private String status;

    @ApiModelProperty(value = "System Client Name", required = true)
    private String sysClientName;

    @ApiModelProperty(value = "Bank Client Name", required = true)
    private String bankClientName;

    @ApiModelProperty(value = "Client ID", required = false)
    private String clientId;
    
    @ApiModelProperty(value = "Recharge ID", required = false)
    private String rechargeId;
    
    @ApiModelProperty(value = "Virtual Account No", required = false)
    private String virtualAccountNo;

    @ApiModelProperty(value = "First File", required = true)
    private String file1;

    @ApiModelProperty(value = "Second File", required = false)
    private String file2;

    @ApiModelProperty(value = "Third File", required = false)
    private String file3;
    
    @ApiModelProperty(value = "Third File", required = false)
    private Date createTime;
    
    @ApiModelProperty(value = "Reason Rejection", required = false)
    private String reasonRejection;
}
