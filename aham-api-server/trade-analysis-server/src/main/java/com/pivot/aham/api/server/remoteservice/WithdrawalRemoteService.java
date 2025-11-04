package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.SaxoToUobOfflineConfirmByExcelDTO;
import com.pivot.aham.api.server.dto.SaxoToUobOfflineConfirmDTO;
import com.pivot.aham.api.server.dto.WithdrawalFromGoalDTO;
import com.pivot.aham.api.server.dto.WithdrawalFromVirtalAccountDTO;
import com.pivot.aham.api.server.dto.res.RedeemApplyResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

/**
 * 提现远程服务
 *
 * @author addison
 * @since 2018年12月10日
 */
public interface WithdrawalRemoteService extends BaseRemoteService {

    /**
     * 从虚拟账户提现
     *
     * @param withdrawalFromVirtalAccountDTO
     * @return
     */
    RpcMessage<RedeemApplyResDTO> withdrawalFromVirtalAccount(WithdrawalFromVirtalAccountDTO withdrawalFromVirtalAccountDTO);

    /**
     * 从goal提现
     *
     * @param param
     * @return
     */
    RpcMessage<RedeemApplyResDTO> withdrawalFromGoal(WithdrawalFromGoalDTO param);

    /**
     * 接口方式saox到uob确认
     *
     * @param saxoToUobOfflineConfirmDTO
     * @return
     */
    RpcMessage saxoToUobOfflineConfirm(SaxoToUobOfflineConfirmDTO saxoToUobOfflineConfirmDTO);

    /**
     * 线下确认saxo到uob转账
     *
     * @param saxoToUobOfflineConfirmByExcelDTO
     * @return
     */
    RpcMessage saxoToUobOfflineConfirmByExcel(SaxoToUobOfflineConfirmByExcelDTO saxoToUobOfflineConfirmByExcelDTO);

}
