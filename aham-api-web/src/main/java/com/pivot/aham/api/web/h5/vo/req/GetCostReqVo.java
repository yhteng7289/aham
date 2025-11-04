package com.pivot.aham.api.web.h5.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class GetCostReqVo {

    private Integer costCountries;
    private Integer costCourse;

}
