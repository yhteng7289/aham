package com.pivot.aham.api.web.admin.vo.req;

import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel("UserEFTAccountReqVo-请求对象说明")
public class UserEFTAccountReqVo extends BaseVo {

    private String pooling;
    private String age;
    private String risk;

}
