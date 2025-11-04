package com.pivot.aham.api.web.web.vo.req;

import com.google.common.collect.Lists;
import com.pivot.aham.common.core.base.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import com.pivot.aham.api.server.dto.req.UobRechargeReq;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 请填写类注释
 *
 * @author dexter
 * @since 20/4/2020
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "insert recharge log")
public class InsertRechargeLogReqVo extends BaseVo {

    @Valid
    @ApiModelProperty(value = "insert recharge log list:uobRechargeReqList", required = true)
    private List<RechargeLogReqVo> uobRechargeReqList;

    @Data
    @Accessors(chain = true)
    @ApiModel(value = "Recharge Log Info")
    public static class RechargeLogReqVo {

        @NotNull(message = "bankOrderNo cannot be empty or null")
        @ApiModelProperty(value = "bankOrderNo", required = true)
        private String bankOrderNo;

        @NotNull(message = "clientName cannot be empty or null")
        @ApiModelProperty(value = "clientName", required = true)
        private String clientName;

        @NotNull(message = "virtualAccountNo cannot be empty or null")
        @ApiModelProperty(value = "virtualAccountNo", required = true)
        private String virtualAccountNo;

        @NotNull(message = "currency cannot be empty or null")
        @ApiModelProperty(value = "currency", required = true)
        private String currency;

        @NotNull(message = "bankOrderNo cannot be empty or null")
        @ApiModelProperty(value = "clientId", required = true)
        private String referenceCode;

        @NotNull(message = "cashAmount cannot be empty or null")
        @ApiModelProperty(value = "cashAmount", required = true)
        private String cashAmount;

        @NotNull(message = "tradeTime cannot be empty or null")
        @ApiModelProperty(value = "tradeTime", required = true)
        private String tradeTime;

    }

    public List<UobRechargeReq> convertToReq() {
        List<UobRechargeReq> uobRechargeReqs = Lists.newArrayList();
        for (RechargeLogReqVo vo : uobRechargeReqList) {

            UobRechargeReq uobRechargeReq = new UobRechargeReq();

            uobRechargeReq.setBankOrderNo(vo.getBankOrderNo());
            uobRechargeReq.setClientName(vo.getClientName());
            uobRechargeReq.setVirtualAccountNo(vo.getVirtualAccountNo());
            uobRechargeReq.setCurrency(vo.getCurrency());
            uobRechargeReq.setReferenceCode(vo.getReferenceCode());
            uobRechargeReq.setCashAmount(vo.getCashAmount());
            uobRechargeReq.setTradeTime(vo.getTradeTime());

            uobRechargeReqs.add(uobRechargeReq);

        }

        return uobRechargeReqs;
    }

}
