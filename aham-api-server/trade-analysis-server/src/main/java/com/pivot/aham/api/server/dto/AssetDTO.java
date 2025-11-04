package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.CurrencyEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * Created by luyang.li on 18/12/9.
 *
 * 用户资产的转化只能基于一个用户,在同一个goal上进行转移
 */
@Data
@Accessors(chain = true)
public class AssetDTO extends BaseDTO {
    private String clientId;
    /**
     * 账户类型：1:美金账户,2:新币账户
     */
    private CurrencyEnum currency;
    /**
     * 虚拟账户号
     */
    private String virtualAccountNo;
    /**
     * 总账户Id
     */
    private Long accountId;
    /**
     */
    private String goalId;
    /**
     * 资产详情
     */
    private List<AssetDetailDTO> assetDetailDTOList;

}
