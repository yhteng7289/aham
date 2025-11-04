package com.pivot.aham.api.service;

import com.pivot.aham.api.server.dto.UobRechargeLogDTO;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.enums.UobTransferOrderTypeEnum;
import java.util.List;

/**
 * Created by hao.tong on 2018/12/12.
 */
public interface UobTradingService {

    /**
     * 创建UOB到个人的转账执行单
     */
    void createTransferExecutionOrderClient();

    /**
     * 创建UOB到SAXO的转账执行单
     */
    void createTransferExecutionOrderSaxo();

    /**
     * 执行UOB到客户的转账单
     */
    void executeTransferOrderClient();

    /**
     * 执行UOB到SAXO的转账单
     */
    void executeTransferOrderSaxo();

    /**
     * 执行UOB到客户的换汇单
     */
    void executeExchangeOrderClient();

    /**
     * 执行UOB到SAXO的换汇单
     */
    void executeExchangeOrderSaxo();

    /**
     * 从文件确认转账
     */
    void confirmTransferOrderClient();

    /**
     * 从saxo确认到账
     */
    void confirmExecutionOrderSaxo();

    /**
     * 从文件确认换汇
     */
    void confirmExchangeOrderClient();

    void confirmExchangeOrderSaxo();

    /**
     * 业务单确认 转账
     */
    void confirmTransferBusinessOrderClient();

    /**
     * 业务单确认 转账
     */
    void confirmTransferBusinessOrderSaxo();

    /**
     * 业务单确认回调 转账
     */
    void notifyTransferBusinessOrderClient();

    void notifyTransferBusinessOrderSaxo();

    /**
     * 业务单确认回调 换汇
     */
    void notifyExchangeOrderClient();

    void notifyExchangeOrderSaxo();

    /**
     * 查询入账记录并入库
     */
    void saveRechargeLog();
    
    RpcMessage<String> insertRechargeLog(List<UobRechargeLogDTO> uobRechargeLogDTO);
    
    void saveRechargeLogFromUOB();
    
    void confirmTransferOrderClientDirect();
    
    void executeTransferOrderToBank(UobTransferOrderTypeEnum orderTypeEnum);
}
