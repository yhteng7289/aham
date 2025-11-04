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
@ApiModel(value = "PdfStatementResVo请求参数")
public class PdfStatementResVo {

    @ApiModelProperty(value = "file", required = true)
    private String fileData;

}
