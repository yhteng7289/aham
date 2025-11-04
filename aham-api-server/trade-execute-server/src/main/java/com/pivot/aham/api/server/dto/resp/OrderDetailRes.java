package com.pivot.aham.api.server.dto.resp;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDetailRes implements Serializable{
	private Long id;
	private String etfCode;
	private Date applyTime;
	private String saxoOrderCode;
	private String orderTypeAhamDesc;
	private BigDecimal applyShare;
}
