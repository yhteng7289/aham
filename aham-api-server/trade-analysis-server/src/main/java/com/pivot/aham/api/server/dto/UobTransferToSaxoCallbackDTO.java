package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseVo;
import com.pivot.aham.common.enums.TransferStatusEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月10日
 */
@Data
@Accessors(chain = true)
public class UobTransferToSaxoCallbackDTO extends BaseVo {
//    private BigDecimal confirmMoney;
//    private BigDecimal confirmShare;
    private Long orderNo;
    private TransferStatusEnum transferStatus;
}
