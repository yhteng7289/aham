package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.AccountTypeEnum;
import com.pivot.aham.common.enums.analysis.InitDayEnum;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 账户实体
 *
 * @author addison
 * @since 2018年12月06日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_info",resultMap = "AccountInfoRes")
public class AccountInfoPO extends BaseModel {
    private AccountTypeEnum investType;
    private String portfolioId;
    private InitDayEnum initDay;

    //查询辅助
    private Long likeAccountId;
    private String goalId;
}
