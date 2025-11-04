package com.pivot.aham.api.service.job.wrapperbean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月20日
 */
@Data
public class ProductWeightBean implements Serializable{
    private Map<String,BigDecimal> mainEtf;
    private Map<String,BigDecimal> subEtf;


}
