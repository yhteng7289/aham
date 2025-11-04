package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import com.pivot.aham.common.enums.ProductMainSubTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 18/12/6.
 */
@Data
@Accessors(chain = true)
public class ProductInfoPO extends BaseModel {
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

}
