package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_goal_info",resultMap = "baseUserGoalInfoMap")
public class UserGoalInfoPO extends BaseModel{
    private String clientId;
    private String goalId;
    private String goalName;
    private String portfolioId;
    private String referenceCode;

    //查询辅助
    private String likeGoalId;
    
    private String deleted; //Added By WooiTatt
}
