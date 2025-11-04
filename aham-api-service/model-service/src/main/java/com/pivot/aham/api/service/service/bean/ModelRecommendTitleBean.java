package com.pivot.aham.api.service.service.bean;

import com.pivot.aham.api.service.mapper.model.ProductInfoPO;
import com.pivot.aham.common.enums.ExcelTitleTypeEnum;
import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import lombok.Data;

/**
 * Created by luyang.li on 18/12/8.
 *
 * 模型数据的第一行详情
 */
@Data
public class ModelRecommendTitleBean {
    private ProductInfoPO productInfo;
    //如果是第一类型 -> 属于哪一种可以类型
    private FirstClassfiyTypeEnum firstClassfiyType;
    private ExcelTitleTypeEnum excelTitleType;

}
