package com.pivot.aham.api.service.mapper;

import com.pivot.aham.api.service.mapper.model.ProductInfoPO;
import com.pivot.aham.common.core.base.BaseMapper;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by luyang.li on 18/12/6.
 */
public interface ProductInfoMapper extends BaseMapper<ProductInfoPO> {
    List<ProductInfoPO> selectTradeProducts(@Param("productStatus") ProductTradeStatusEnum productStatus);

    List<ProductInfoPO> listProductInfo(ProductInfoPO productInfo);

}
