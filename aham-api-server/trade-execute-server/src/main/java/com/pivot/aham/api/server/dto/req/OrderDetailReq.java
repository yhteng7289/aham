package com.pivot.aham.api.server.dto.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.CurrencyEnum;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@Accessors(chain = true)
@ApiModel(value = "OrderDetailReq")
public class OrderDetailReq{
	
    @ApiModelProperty(value = "orderDetailReqList", required = true)
	List<OrderDetailR> orderDetailReqList;
	
	@Data
    @Accessors(chain = true)
    @ApiModel(value = "OrderDetailR")
    public static class OrderDetailR{
        @ApiModelProperty(value = "id", required = true)
		private Long id;
        @ApiModelProperty(value = "etfCode", required = false)
		private String etfCode;
        @ApiModelProperty(value = "applyTime", required = false)
		private Date applyTime;
        @ApiModelProperty(value = "saxoOrderCode", required = false)
		private String saxoOrderCode;
        @ApiModelProperty(value = "orderTypeAhamDesc", required = true)
		private String orderTypeAhamDesc;
        @ApiModelProperty(value = "applyShare", required = false)
		private BigDecimal applyShare;
        @ApiModelProperty(value = "confirmShare", required = true)
		private BigDecimal confirmShare;
        @ApiModelProperty(value = "confirmAmount", required = true)
		private BigDecimal confirmAmount;
	}		
}
