package com.pivot.aham.api.web.web.vo.res;

import lombok.Data;

import java.util.List;

/**
 * Created by luyang.li on 18/12/9.
 */
@Data
public class EtfListBeanVo {
    private List<EtfPercentageVo> mainEtf;
    private List<EtfPercentageVo> subEtf;
}
