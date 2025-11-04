package com.pivot.aham.api.service.service;

import com.pivot.aham.api.service.mapper.model.ProductInfoPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/6.
 */
public interface ProductInfoService extends BaseService<ProductInfoPO> {
    Map<String,ProductInfoPO> getPorductInfoMap();

    List<ProductInfoPO> listProductInfo(ProductInfoPO productInfo);
}
