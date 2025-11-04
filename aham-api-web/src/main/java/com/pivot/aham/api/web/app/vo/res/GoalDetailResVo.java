package com.pivot.aham.api.web.app.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GoalDetailResVo {

    private String goalId;

    private String refCode;
}
