package com.pivot.aham.api.web.web.vo.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "success TNCF")
public class TAccountRedeemResVo {

    private BigDecimal tncf;
    private List<BigDecimal> redeemAmount;
    private List<String> redeemClient;
    private int total;

    public void setTpcf(BigDecimal inTNCF) {

        this.tncf = inTNCF;

    }

    public void setRedeemAmount(List<BigDecimal> inRedeemAmount) {

        this.redeemAmount = inRedeemAmount;

    }

    public void setRedeemClient(List<String> inRedeemClient) {

        this.redeemClient = inRedeemClient;

    }

    public void setTotal(int inTotal) {

        this.total = inTotal;

    }

}
