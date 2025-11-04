package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by luyang.li on 18/11/30.
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_info",resultMap = "baseUserInfoMap")
public class UserInfo extends BaseModel {
    private String clientId;
    private String clientName;
    private String address;
    private String mobileNum;

    //查询辅助
    private String likeClientId;
    private String likeClientName;

}
