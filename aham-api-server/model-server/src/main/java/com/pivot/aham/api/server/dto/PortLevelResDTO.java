package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by luyang.li on 19/2/25.
 */
@Data
@Accessors(chain = true)
public class PortLevelResDTO extends BaseVo {

    private Date date;
    private BigDecimal portfolioLevel;
    //对标收益数据
    private BigDecimal benchmarkData;

}
