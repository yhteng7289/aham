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
@ApiModel(value = "好友详情")
public class FriendResVo {

    @ApiModelProperty(value = "分享地址", required = true)
    private String url;

    @ApiModelProperty(value = "好友名", required = true)
    private String friendFirstName;

    @ApiModelProperty(value = "好友姓", required = true)
    private String friendLastName;

    @ApiModelProperty(value = "状态", required = true)
    private String status;

    @ApiModelProperty(value = "客户id", required = true)
    private String clientId;
}
