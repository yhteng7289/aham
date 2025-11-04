package com.pivot.aham.api.service.job.wrapperbean;

import com.pivot.aham.api.service.mapper.model.TmpOrderRecordPO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
@Data
@Accessors(chain = true)
public class BuyEtfTmpOrderBean {

    private List<TmpOrderRecordPO> successTmpOrders;
    private List<TmpOrderRecordPO> failTmpOrders;
}
