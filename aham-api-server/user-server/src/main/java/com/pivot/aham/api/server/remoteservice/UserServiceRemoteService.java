package com.pivot.aham.api.server.remoteservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/2.
 */
public interface UserServiceRemoteService extends BaseRemoteService {

    /**
     * 根据clientId查询用户信息
     *
     * @param clientId
     * @return
     */
    UserInfoResDTO queryByClientId(String clientId);

    /**
     * 查询用户列表
     *
     * @return
     */
    List<UserInfoResDTO> queryUserList();

    /**
     * 保存FE 同步过来的用户信息
     *
     * @param userInfoDTO
     */
    void saveUserInfo(UserInfoDTO userInfoDTO);

    /**
     * 同步用户基本信息
     *
     * @param userInfoDTO
     */
    void userBaseInfo(UserInfoDTO userInfoDTO);

    /**
     * 批量保存用户基础信息
     *
     * @param userBaseInfoDTOs
     */
    //void saveUserBaseInfos(List<UserBaseInfoDTO> userBaseInfoDTOs);
    RpcMessage<String> saveUserBaseInfos(List<UserBaseInfoDTO> userBaseInfoDTOs);

    /**
     * 批量修改用户基础信息
     *
     * @param userBaseInfoDTOS
     */
    void updateUserBaseInfos(List<UserBaseInfoDTO> userBaseInfoDTOS);

    /**
     * 保存用户goal信息
     *
     * @param userGoalInfoDTOs
     */
    void saveUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs);

    /**
     * 保存用户goal信息
     *
     * @param userGoalInfoDTOs
     */
    void updateUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs);

    /**
     *
     * @param clientId
     * @param goalId
     */
    void updateDeletedByClientIdAndGoalId(String clientId, String goalId);

    /**
     * 用户在goal上设置金额
     *
     * @param userSetGoalMoneyDTOs
     */
    List<UserSetGoalMoneyResDTO> userSetGoalMoney(List<UserSetGoalMoneyDTO> userSetGoalMoneyDTOs);

    /**
     * 根据虚拟账号No查询用户虚拟账户信息
     *
     * @param virtualAccountNo
     * @return
     */
    BankVirtualAccountResDTO queryByVirtualAccountNo(String virtualAccountNo);

    /**
     * 查询某时间段的余额流水
     *
     * @param bankVirtualAccountDailyRecordDTO
     * @return
     */
    List<BankVirtualAccountDailyRecordResDTO> queryBankVirtualAccountDailyRecordList(
            BankVirtualAccountDailyRecordDTO bankVirtualAccountDailyRecordDTO);

    /**
     * 查询虚拟订单流水
     *
     * @param bankVirtualAccountOrderDTO
     */
    BankVirtualAccountOrderResDTO queryVAOrder(BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO);

    /**
     * UOB到SAXO转账成功修改状态
     *
     * @param bankVirtualAccountOrderDTO
     * @param clientId
     */
    void transferFinish(BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO, String clientId);

    void saveOrdersAndUpdateAccount(List<BankVirtualAccountOrderDTO> vAOrderList, String clientId);

    List<BankVirtualAccountResDTO> queryListBankVirtualAccount(BankVirtualAccountDTO bankVirtualAccountDTO);

    BankVirtualAccountOrderResDTO queryById(Long id);

    /**
     * 更新虚拟订单状态
     *
     * @param virtualAccountOrderDTO
     */
    void updateVAOrder(BankVirtualAccountOrderDTO virtualAccountOrderDTO);

    RpcMessage<List<BankVirtualAccountOrderResDTO>> listBankVirtualAccountOrders(BankVirtualAccountOrderDTO dto);

    RpcMessage<Page<BankVirtualAccountOrderResDTO>> queryBankVirtualAccountOrderPage(BankVirtualAccountOrderDTO dto);

    /**
     * UOB充值 exchange USD->SGD 回调
     *
     * @param uobExchangeDTO
     */
    void handelUobExchangeCallBack(UobExchangeDTO uobExchangeDTO);

    /**
     * 查询用户基本信息
     *
     * @param userGoalInfoDTO
     * @return
     */
    RpcMessage<UserGoalInfoResDTO> getUserGoalInfo(UserGoalInfoDTO userGoalInfoDTO);
    
    /**
     * 查询用户基本信息
     *
     * @param userGoalInfoDTO
     * @return
     */
    RpcMessage<UserGoalInfoResDTO> getUserGoalInfoForStatement(UserGoalInfoDTO userGoalInfoDTO);

    /**
     * 查询用户goal信息
     *
     * @param userGoalInfoDTO
     * @return
     */
    RpcMessage<Page<UserGoalInfoResDTO>> getUserGoalInfoPage(UserGoalInfoDTO userGoalInfoDTO);

    /**
     * 查询用户goal信息
     *
     * @param userGoalInfoDTO
     * @return
     */
    RpcMessage<List<UserGoalInfoResDTO>> getUserGoalInfoList(UserGoalInfoDTO userGoalInfoDTO);

    /**
     * 改账户上首次充值币种类型
     *
     * @param virtualAccountDTO
     * @return
     */
    RpcMessage<BankVirtualAccountOrderResDTO> queryFirstBVAOrder(String virtualAccountDTO);

    /**
     * 查询用户
     *
     * @param userInfoDTO
     * @return
     */
    RpcMessage<List<UserInfoResDTO>> queryUserInfos(UserInfoDTO userInfoDTO);

    /**
     * 查询用户
     *
     * @param userInfoDTO
     * @return
     */
    RpcMessage<Page<UserInfoResDTO>> queryUserInfoPage(UserInfoDTO userInfoDTO);

    void UobRechargeSyncJobImpl();

    List<BankVirtualAccountOrderBalDTO> getListByTradeTime(BankVirtualAccountOrderDTO params);

    List<BankVirtualAccountOrderResDTO> getByTradeTime(BankVirtualAccountOrderDTO params);

    List<BankVirtualAccountBalDTO> getAccountBalByTradeTime(Map<String, Object> params);

    //void staticBankVirtualAccountJob();

    void fixBankOrder();
    
    void updateUserRechargeStatus(Long getId, Long setId, UserRechargeStatusEnum status);
    
    void updateUserRechargeStatusToSuccess(Long accountRechargeId);
    
    BigDecimal getPendingDeposit(String clientId, String goalId); // Added By WooiTatt
    
    
}
