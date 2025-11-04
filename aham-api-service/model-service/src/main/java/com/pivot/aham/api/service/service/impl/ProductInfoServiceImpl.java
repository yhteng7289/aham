package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.service.mapper.ProductInfoMapper;
import com.pivot.aham.api.service.mapper.model.ProductInfoPO;
import com.pivot.aham.api.service.service.ProductInfoService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/6.
 */
@CacheConfig(cacheNames = "productInfo")
@Service
public class ProductInfoServiceImpl extends BaseServiceImpl<ProductInfoPO, ProductInfoMapper> implements ProductInfoService {

    @Override
    public Map<String, ProductInfoPO> getPorductInfoMap() {
        List<ProductInfoPO> productInfoList = mapper.selectTradeProducts(ProductTradeStatusEnum.TRADE);
        if (CollectionUtils.isEmpty(productInfoList)) {
            return null;
        }
        return productInfoList.stream().collect(Collectors.toMap(ProductInfoPO::getProductCode , item -> item));
    }

    @Override
    public List<ProductInfoPO> listProductInfo(ProductInfoPO productInfo) {
        return mapper.listProductInfo(productInfo);
    }
}
