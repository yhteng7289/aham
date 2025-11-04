package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_etf_shares",resultMap = "UserEtfSharesRes")
public class UserEtfSharesPO extends BaseModel {
    private Date staticDate;
    private Long accountId;
    private String clientId;
    private String goalId;
    private String productCode;
    private BigDecimal shares;
    private BigDecimal money;
    private Date startStaticDate;
    private Date endStaticDate;


}
