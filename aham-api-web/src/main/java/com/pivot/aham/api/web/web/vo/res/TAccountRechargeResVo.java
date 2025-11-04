package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "success TPCF")
public class TAccountRechargeResVo {

    private BigDecimal tpcf;
    private List<BigDecimal> rechargeAmount;
    private List<String> rechargeClient;
    private int total;

    public void setTpcf(BigDecimal inTPCF) {

        this.tpcf = inTPCF;

    }

    public void setRechargeAmount(List<BigDecimal> inRechargeAmount) {

        this.rechargeAmount = inRechargeAmount;

    }

    public void setRechargeClient(List<String> inRechargeClient) {

        this.rechargeClient = inRechargeClient;

    }

    public void setTotal(int inTotal) {

        this.total = inTotal;

    }

}
