package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ReferResVo {

    @ApiModelProperty(value = "朋友列表", required = true)
    private List<FriendResVo> friendList;

}
