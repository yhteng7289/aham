package com.pivot.aham.api.web.web.vo.res;

import com.pivot.aham.api.server.dto.ClassfiyEtfWrapper;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by luyang.li on 19/2/26.
 */
@Data
@Accessors(chain = true)
public class PortFutureLevelVo {

    private List<PortFutureLevelDetail> futurePortLevelDetails;
    private List<ClassfiyEtfWrapper> modelData;

}
