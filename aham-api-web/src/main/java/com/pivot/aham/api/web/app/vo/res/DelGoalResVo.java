package com.pivot.aham.api.web.app.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "删除目标返回参数")
public class DelGoalResVo {

    private String gaolId;

    private String clientId;

}
