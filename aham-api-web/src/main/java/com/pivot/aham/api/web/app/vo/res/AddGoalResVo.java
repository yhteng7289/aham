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
@ApiModel(value = "添加目标返回参数")
public class AddGoalResVo {

    private String referCode;

    private String clientId;

    private String accountNum;

    private String bankCode;

    private String branchCode;

    private String swiftCode;

    private String bankAddress;

    private String bankName;

    private String recipientName;

}
