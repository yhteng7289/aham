package com.pivot.aham.api.service.service;

import com.pivot.aham.api.server.dto.UobExchangeDTO;
import com.pivot.aham.api.service.bean.GoalSetMoneyBean;
import com.pivot.aham.api.service.bean.RechargeRefundBean;
import com.pivot.aham.api.service.mapper.model.UserGoalInfoPO;

import java.util.List;

/**
 * Created by luyang.li on 2018/12/24.
 */
public interface UobRechargeService {

    /**
     * 同步UOB线下入金到松鼠虚拟账户
     */
    void syncUobRechargeToVirtualAccount();

    /**
     * 同步UOB线下入金，UOB内部购汇USD转SGD回调处理
     *
     * @param uobExchangeDTO
     */
    void handelUobExchangeCallBack(UobExchangeDTO uobExchangeDTO);

    /**
     * 线下用户手动入金
     *
     * @param goalSetMoneyBean
     * @param userGoal
     * @return
     */
    Long handelGoalSetMoney(GoalSetMoneyBean goalSetMoneyBean, UserGoalInfoPO userGoal);

    void notifyRechargeRefund(List<RechargeRefundBean> needRefundUsers);

}
