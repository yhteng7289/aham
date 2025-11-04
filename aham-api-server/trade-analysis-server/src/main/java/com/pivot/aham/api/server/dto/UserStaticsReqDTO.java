package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserStaticsReqDTO extends BaseDTO {
    private Long accountId;
    private String clientId;
    private String goalId;
    private Date staticDate;

    private List<String> goalIdList;
}
