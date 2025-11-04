/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.in.vo;

import com.google.common.io.Files;
import com.pivot.aham.common.enums.NameAliasManageStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.File;
import lombok.Data;

@Data
@ApiModel("NameAliasManageProcessReqVo-请求对象说明")
public class NameAliasManageProcessReqVo {

    @ApiModelProperty(value = "Status", required = true)
    private NameAliasManageStatusEnum status;

    @ApiModelProperty(value = "System Client Name", required = false)
    private String systemClientName;

    @ApiModelProperty(value = "Bank Client Name", required = false)
    private String bankClientName;

    @ApiModelProperty(value = "Client ID", required = false)
    private String clientId;

    @ApiModelProperty(value = "First File", required = true)
    private String file1;

    @ApiModelProperty(value = "Second File", required = false)
    private String file2;

    @ApiModelProperty(value = "Third File", required = false)
    private String file3;
    
    private Files fileme;
    
    private File fileme2;
    
    private Integer pageNo;
    
    private Integer pageSize;
    
    private String rechargeId;
    
    private String reasonRejection;
    
}
