package com.pivot.aham.api.web.h5.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class StatementResVo {
    private String month;
    private String statementUrl;
}
