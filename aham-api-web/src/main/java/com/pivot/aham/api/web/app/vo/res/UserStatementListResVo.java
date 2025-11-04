package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "UserStatementLis请求参数")
public class UserStatementListResVo {

    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;

    @ApiModelProperty(value = "fileId", required = true)
    private String fileId;

    @ApiModelProperty(value = "fileName", required = true)
    private String fileName;

    @ApiModelProperty(value = "fileDownloadedDate", required = true)
    private String fileDownloadedDate;

}
