package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * Created by luyang.li on 18/12/9.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户订单查询请求参数")
public class UserOrdersResVo {

    @ApiModelProperty(value = "clientId", required = true)
    private String clientId;
    private Long orderNo;
    private BigDecimal money;
    // 1-investment, 2-withdraw
    private Integer orderType;    
    private String orderTypeDesc;
    private String orderTime;
    private String goalId;
    // 1-Completed, 2-Processing, 3-Failed
    private Integer orderStatus;    
    private String orderStatusDesc;

}
