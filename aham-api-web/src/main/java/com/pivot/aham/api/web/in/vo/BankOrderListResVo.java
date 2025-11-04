package com.pivot.aham.api.web.in.vo;

import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("BankOrderResVo-请求对象说明")
public class BankOrderListResVo {

    private BigDecimal totalAvaiableSgd;
    private BigDecimal totalAvaiableUsd;
    private Page<BankOrderResVo> page;

}
