package com.pivot.aham.api.service.job.impl;

import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.mapper.model.SaxoToUobTotalRecordPO;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.api.service.service.SaxoToUobTotalRecordService;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理用户提现划款
 *
 * @author addison
 * @since 2018年12月06日
 */
@Component
@Scope(value = "prototype")
@Data
public class WithdrawalSaxoToUobTransSupport{
    private List<RedeemApplyPO> redeemApplyPOList;
    private SaxoToUobTotalRecordPO saxoToUobTotalRecord;
    @Autowired
    private RedeemApplyService bankVARedeemService;
    @Resource
    private SaxoToUobTotalRecordService saxoToUobTotalRecordService;

    @Transactional(rollbackFor = Throwable.class)
    public void withdrawalSaxoToUob() {
        if(CollectionUtils.isNotEmpty(redeemApplyPOList)){
            for(RedeemApplyPO redeemApplyPO:redeemApplyPOList) {
                bankVARedeemService.updateOrInsert(redeemApplyPO);
            }
        }
        saxoToUobTotalRecordService.updateOrInsert(saxoToUobTotalRecord);
    }
}
