package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by luyang.li on 19/3/5.
 */
@Data
@Accessors(chain = true)
public class PortFutureLevelResWrapper extends BaseDTO {

    private List<PortFutureLevelResDTO> futurePortLevelDetails;
    private List<ClassfiyEtfWrapper> modelData;

}
