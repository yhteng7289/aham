package com.pivot.aham.api.web.app.vo.res;

import com.pivot.aham.api.web.web.vo.res.UserOrdersResVo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "用户订单查询请求参数")
public class AppUserOrdersResVo {

    private String date;
    
    private Integer dateNumber;
    
    private List<UserOrdersResVo> userOrdersResVos;
}
