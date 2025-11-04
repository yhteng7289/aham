package com.pivot.aham.api.service.mapper.model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.analysis.DividendHandelStatusEnum;
import com.pivot.aham.common.enums.analysis.DividendHandelTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月21日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_dividend",resultMap = "AccountDividendRes") //Added By WooiTatt
public class UserDividendPO extends BaseModel{
    private String clientId;
    private Long accountId;
    private String goalId;
    private Date dividendDate;
    private BigDecimal dividendAmount;
    private DividendHandelStatusEnum handelStatus;
    private DividendHandelTypeEnum handelType;
    private String productCode;
    private String dividendOrderId;

    private Date startDividendDate;
    private Date endDividendDate;
    public UserDividendPO(Date dividendDate, DividendHandelTypeEnum handelType) {
        this.dividendDate = dividendDate;
        this.handelType = handelType;
    }

    public UserDividendPO() {
    }
}
