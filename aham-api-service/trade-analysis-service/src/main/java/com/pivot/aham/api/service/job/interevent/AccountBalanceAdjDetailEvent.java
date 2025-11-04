package com.pivot.aham.api.service.job.interevent;

import com.pivot.aham.api.server.dto.EtfCallbackDTO;
import lombok.Data;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年01月22日
 */
@Data
public class AccountBalanceAdjDetailEvent {
    List<EtfCallbackDTO> etfCallbackDTOList;
}
