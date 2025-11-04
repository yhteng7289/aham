package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class ProductInfoResDTO extends BaseDTO {

    private String productCode;
    private String productName;
    //产品一级分类
    private FirstClassfiyTypeEnum firstClassfiyType;
    //产品一级分类描述
    private String firstClassfiyDesc;
    //产品描述
    private String productDesc;
    //产品类型 mian_Invesment, sub_Invesment
    private ProductMainSubTypeEnum productType;
    //产品状态
    private ProductTradeStatusEnum productStatus;
    //产品介绍连接
    private String url;
    
    private Date bsnDt;
    
    private BigDecimal closingPrice;
    
    private Date navDate;
}
