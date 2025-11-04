package com.pivot.aham.api.web.web.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ModelRecommendReqVo请求对象说明")
public class PortLevelResVo {
    private Date date;
    //模型收益
    private BigDecimal portfolioLevel;
    //对标收益数据
    private BigDecimal benchmarkData;


}
