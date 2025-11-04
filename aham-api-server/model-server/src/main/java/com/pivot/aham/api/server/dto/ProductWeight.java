package com.pivot.aham.api.server.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by luyang.li on 19/1/22.
 */
@Data
@Accessors(chain = true)
public class ProductWeight implements Serializable {

    private String etf;
    @JSONField(serializeUsing = CustomDoubleIntSerialize.class)
    private BigDecimal weight;
    private String url;
}
