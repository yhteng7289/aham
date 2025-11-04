package com.pivot.aham.api.server.dto;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月02日
 */
@Data
public class SaxoToUobOfflineConfirmDTO  extends BaseDTO {
    /**
     * 批次id
     */
    private String saxoToUobBatchId;
    /**
     * uob订单号
     */
    private String saxoToUobOrderId;


}
