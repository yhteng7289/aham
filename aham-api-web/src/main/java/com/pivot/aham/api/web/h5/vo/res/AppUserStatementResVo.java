package com.pivot.aham.api.web.h5.vo.res;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class AppUserStatementResVo {

    private String year;

    private List<StatementResVo> statements;
}
