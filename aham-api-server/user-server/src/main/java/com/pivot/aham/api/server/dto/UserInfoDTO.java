package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
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
public class UserInfoDTO extends BaseDTO{
    private String clientId;
    private String clientName;
    private String address;
    private String mobileNum;

    private Integer pageNo;
    private Integer pageSize;

    //查询辅助
    private String likeClientId;
    private String likeClientName;

}
