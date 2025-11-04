package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.bean.GoalSetMoneyBean;
import com.pivot.aham.api.service.job.impl.FixBankOrder;
//import com.pivot.aham.api.service.job.impl.StaticBankVirtualAccountJobImpl;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.context.Resources;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DataUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderActionTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import com.pivot.aham.common.enums.recharge.UserRechargeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Service(interfaceClass = UserServiceRemoteService.class)
@Slf4j
public class UserServiceRemoteServiceImpl implements UserServiceRemoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceRemoteServiceImpl.class);

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private BankVirtualAccountService bankVirtualAccountService;
    @Resource
    private UserGoalInfoInfoService userGoalInfoInfoService;
    @Resource
    private BankVirtualAccountOrderService bankVirtualAccountOrderService;
    @Resource
    private UobRechargeService uobRechargeService;
    @Autowired
    private BankVirtualAccountDailyRecordService bankVirtualAccountDailyRecordService;
    //@Resource
    //private StaticBankVirtualAccountJobImpl staticBankVirtualAccountJob;
    @Resource
    private FixBankOrder fixBankOrder;
    @Resource
    private UserRechargeStatusService userRechargeStatusService;

    @Override
    public UserInfoResDTO queryByClientId(String clientId) {
        UserInfo userInfo = userInfoService.queryByClientId(clientId);
        if (null == userInfo) {
            throw new BusinessException("不存在的用户");
        }
        return BeanMapperUtils.map(userInfo, UserInfoResDTO.class);
    }

    @Override
    public List<UserInfoResDTO> queryUserList() {
        UserInfo userInfo = new UserInfo();
        List<UserInfo> userInfos = userInfoService.queryList(userInfo);
        return BeanMapperUtils.mapList(userInfos, UserInfoResDTO.class);
    }

    @Override
    public void saveUserInfo(UserInfoDTO userInfoDTO) {
        userInfoService.saveUserInfo(userInfoDTO);
    }

    @Override
    public void userBaseInfo(UserInfoDTO userInfoDTO) {
        UserInfo dto = userInfoService.queryByClientId(userInfoDTO.getClientId());
        if (dto == null) {
            saveUserInfo(userInfoDTO);
        } else {
            throw new IllegalArgumentException(Resources.getMessage("CLIENTID_EXISTS"));
        }
    }

    @Override
    public RpcMessage<String> saveUserBaseInfos(List<UserBaseInfoDTO> userBaseInfoDTOs) {
        List<String> clientIds = Lists.newArrayList();
        List<String> virtualAccountNos = Lists.newArrayList();
        List<UserInfoDTO> userInfoDTOList = Lists.newArrayList();
        List<UserInfoDTO> userInsideInfoDTOList = Lists.newArrayList();

        List<BankVirtualAccountDTO> virtualAccountDTOList = Lists.newArrayList();
        try {
            for (UserBaseInfoDTO userBaseInfoDTO : userBaseInfoDTOs) {
                virtualAccountNos.add(userBaseInfoDTO.getBankVirtualAccountDTO().getVirtualAccountNo());
                virtualAccountDTOList.add(userBaseInfoDTO.getBankVirtualAccountDTO());
                if (clientIds.contains(userBaseInfoDTO.getUserInfoDTO().getClientId())) {
                    continue;
                }
                clientIds.add(userBaseInfoDTO.getUserInfoDTO().getClientId());
                userInfoDTOList.add(userBaseInfoDTO.getUserInfoDTO());
                userInsideInfoDTOList.add(userBaseInfoDTO.getUserInfoDTO());
            }
            //查询已经存在的userInfo 和 bankVirtualAccount
            List<UserInfoDTO> alreadyUserInfoDtoList = Lists.newArrayList();
            List<BankVirtualAccount> alreadyVirtualAccountDtoList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(clientIds)) {
                alreadyUserInfoDtoList = userInfoService.queryListByClients(clientIds);
                alreadyVirtualAccountDtoList = bankVirtualAccountService.queryListByVirtualAccounts(virtualAccountNos);
            }
            //本次同步的UserInfo有包含已经存在的数据,过滤掉重复添加的数据
            if (CollectionUtils.isNotEmpty(alreadyUserInfoDtoList)) {
                List<String> alreadyClientIds = alreadyUserInfoDtoList.stream().map(UserInfoDTO::getClientId).
                        collect(Collectors.toList());
                userInfoDTOList = userInfoDTOList.stream().filter(item -> !alreadyClientIds.contains(item.getClientId())).
                        collect(Collectors.toList());
                userInsideInfoDTOList = userInsideInfoDTOList.stream().filter(item -> alreadyClientIds.contains(item.getClientId())).
                        collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(userInfoDTOList)) {
                userInfoService.saveBatch(userInfoDTOList);
            }
            if (CollectionUtils.isNotEmpty(userInsideInfoDTOList)) {

                for (int i = 0; i < userInsideInfoDTOList.size(); i++) {
                    userInfoService.updateUserInfo(userInsideInfoDTOList.get(i));
                }
            }
            //过滤掉重复添加的数据
            if (CollectionUtils.isNotEmpty(alreadyVirtualAccountDtoList)) {
                List<String> alreadyClientIds = alreadyVirtualAccountDtoList.stream().map(BankVirtualAccount::getClientId).
                        collect(Collectors.toList());
                virtualAccountDTOList = virtualAccountDTOList.stream().filter(item -> !alreadyClientIds.
                        contains(item.getClientId())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(virtualAccountDTOList)) {
                bankVirtualAccountService.saveBatch(virtualAccountDTOList);
            }
        } catch (Exception e) {
            return RpcMessage.error(500, e.toString());
        }

        return RpcMessage.success("success");
    }

    @Override
    public void updateUserBaseInfos(List<UserBaseInfoDTO> userBaseInfoDTOs) {
        for (UserBaseInfoDTO userBaseInfo : userBaseInfoDTOs) {
            userInfoService.updateUserInfo(userBaseInfo.getUserInfoDTO());
        }
    }

    @Override
    public void saveUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs) {
        for (UserGoalInfoDTO dto : userGoalInfoDTOs) {
            UserGoalInfoPO userGoalInfoParam = new UserGoalInfoPO();
            userGoalInfoParam.setClientId(dto.getClientId());
            userGoalInfoParam.setPortfolioId(dto.getPortfolioId());
            userGoalInfoParam.setGoalId(dto.getGoalId());
            userGoalInfoParam.setReferenceCode(dto.getReferenceCode());
            userGoalInfoParam.setGoalName(dto.getGoalName());
            UserGoalInfoPO alreadyGoalInfo = userGoalInfoInfoService.getUserGoal(userGoalInfoParam);
            if (null != alreadyGoalInfo) {
                LOGGER.error("clientId:{},同步过来的goal信息里有已经存在的goal,userGoalInfoDTOs:{}",
                        alreadyGoalInfo.getClientId(), JSON.toJSON(userGoalInfoDTOs));
                throw new IllegalArgumentException("该clientId的goal已存在.");
            }
        }
        userGoalInfoInfoService.saveUserGoalInfos(userGoalInfoDTOs);
    }

    @Override
    public void updateUserGoalInfos(List<UserGoalInfoDTO> userGoalInfoDTOs) {
        for (UserGoalInfoDTO dto : userGoalInfoDTOs) {
            UserGoalInfoPO userGoalInfoParam = new UserGoalInfoPO();
            userGoalInfoParam.setClientId(dto.getClientId());
            userGoalInfoParam.setGoalId(dto.getGoalId());
            UserGoalInfoPO alreadyGoalInfo = userGoalInfoInfoService.getUserGoal(userGoalInfoParam);
            if (null == alreadyGoalInfo) {
                LOGGER.error("clientId:{},同步过来的goal信息里有已经存在的goal,userGoalInfoDTOs:{}",
                        alreadyGoalInfo.getClientId(), JSON.toJSON(userGoalInfoDTOs));
                throw new IllegalArgumentException("该clientId的goal不存在.");
            }
            alreadyGoalInfo.setGoalName(dto.getGoalName());
            userGoalInfoInfoService.updateOrInsert(alreadyGoalInfo);
        }

    }

    private List<UserGoalInfoPO> queryUserGoalInfos(UserGoalInfoDTO userGoalInfoDTO) {
        UserGoalInfoPO userGoalInfoPO = new UserGoalInfoPO();
        userGoalInfoPO.setClientId(userGoalInfoDTO.getClientId());
        List<UserGoalInfoPO> userGoalInfoPOs = userGoalInfoInfoService.queryUserGoalInfos(userGoalInfoPO);
        return userGoalInfoPOs;
    }

    /**
     * 1.校验金额 2.修改t_bank_virtual_account 冻结金额可用金额
     * 3.记录t_bank_virtual_account_order 冻结流水
     *
     * @param userSetGoalMoneyDTOList
     * @return
     */
    @Override
    public List<UserSetGoalMoneyResDTO> userSetGoalMoney(List<UserSetGoalMoneyDTO> userSetGoalMoneyDTOList) {

        return handelUserSetGoalMoney(userSetGoalMoneyDTOList);

    }

    /**
     * 用户主动在goal上设置金额 1、bank_virtual_account_order(考虑 SGD -> USD 购汇) 2、重新计算可用金额
     * bank_virtual_account
     *
     * @param userSetGoalMoneyDTOList
     */
    private List<UserSetGoalMoneyResDTO> handelUserSetGoalMoney(List<UserSetGoalMoneyDTO> userSetGoalMoneyDTOList) {
        List<UserSetGoalMoneyResDTO> setGoalMoneyResDTOs = Lists.newArrayList();
        for (UserSetGoalMoneyDTO userSetGoalMoneyDTO : userSetGoalMoneyDTOList) {
            /**
             * 基础信息设定,check
             */
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setClientId(userSetGoalMoneyDTO.getClientId());
            List<UserGoalInfoPO> userGoalInfoList = queryUserGoalInfos(userGoalInfoDTO);
            Map<String, UserGoalInfoPO> userGoalInfoMap = userGoalInfoList.stream().collect(Collectors.toMap(UserGoalInfoPO::getGoalId, account -> account));
            UserGoalInfoPO userGoal = userGoalInfoMap.get(userSetGoalMoneyDTO.getGoalId());
            if (userGoal == null) {
                LOGGER.error("clientId:{},在goal上设置金额没有查询到goal信息", userSetGoalMoneyDTO.getClientId());
                throw new BusinessException(Resources.getMessage("GOAL_NOT_EXISTS"));
            }
            GoalSetMoneyBean goalSetMoneyBean = getGoalSetMoneyBean(userSetGoalMoneyDTO, userGoal);
            Long transNo = uobRechargeService.handelGoalSetMoney(goalSetMoneyBean, userGoal);

            UserSetGoalMoneyResDTO setGoalMoneyResDTO = new UserSetGoalMoneyResDTO();
            setGoalMoneyResDTO.setMoney(userSetGoalMoneyDTO.getMoney());
            setGoalMoneyResDTO.setTransNo(transNo);
            setGoalMoneyResDTO.setClientId(userSetGoalMoneyDTO.getClientId());
            setGoalMoneyResDTO.setGoalId(userSetGoalMoneyDTO.getGoalId());
            setGoalMoneyResDTOs.add(setGoalMoneyResDTO);
        }

        return setGoalMoneyResDTOs;
    }

    private GoalSetMoneyBean getGoalSetMoneyBean(UserSetGoalMoneyDTO userSetGoalMoneyDTO,
            UserGoalInfoPO userGoal ) {

        GoalSetMoneyBean dto = new GoalSetMoneyBean();
        dto.setCurrency(userSetGoalMoneyDTO.getCurrencyType());
        dto.setPortfolioId(userGoal.getPortfolioId());
        dto.setGoalId(userGoal.getGoalId());
        dto.setClientId(userSetGoalMoneyDTO.getClientId());
//        dto.setVirtualAccountNo(bankVirtualAccount.getVirtualAccountNo());
        dto.setMoney(userSetGoalMoneyDTO.getMoney());
        dto.setReferenceCode(userGoal.getReferenceCode());
//        dto.setSgdVirtualAccountNo(bankVirtualAccountMap.get(CurrencyEnum.SGD).getVirtualAccountNo());
        return dto;
    }

    @Override
    public BankVirtualAccountResDTO queryByVirtualAccountNo(String virtualAccountNo) {
        BankVirtualAccount queryParam = new BankVirtualAccount();
        queryParam.setVirtualAccountNo(virtualAccountNo);
        BankVirtualAccount bankVirtualAccount = bankVirtualAccountService.quaryBankVirtualAccount(queryParam);
        if (null == bankVirtualAccount) {
            return null;
        }
        return BeanMapperUtils.map(bankVirtualAccount, BankVirtualAccountResDTO.class);
    }

    @Override
    public BankVirtualAccountOrderResDTO queryVAOrder(BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO) {
        BankVirtualAccountOrder bankVirtualAccountOrder = new BankVirtualAccountOrder();
        bankVirtualAccountOrder.setVirtualAccountNo(bankVirtualAccountOrderDTO.getVirtualAccountNo());
        bankVirtualAccountOrder.setBankOrderNo(bankVirtualAccountOrderDTO.getBankOrderNo());
        bankVirtualAccountOrder.setOrderStatus(bankVirtualAccountOrderDTO.getOrderStatus());
        bankVirtualAccountOrder.setRedeemApplyId(bankVirtualAccountOrderDTO.getRedeemApplyId());
        bankVirtualAccountOrder.setOperatorType(bankVirtualAccountOrderDTO.getOperatorType());
        bankVirtualAccountOrder.setActionType(bankVirtualAccountOrderDTO.getActionType());
        bankVirtualAccountOrder = bankVirtualAccountOrderService.queryVAOrder(bankVirtualAccountOrder);
        if (null == bankVirtualAccountOrder) {
            return null;
        }
        return BeanMapperUtils.map(bankVirtualAccountOrder, BankVirtualAccountOrderResDTO.class);
    }

    @Override
    @Transactional
    public void transferFinish(BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO, String clientId) {
        BankVirtualAccountOrder bankVirtualAccountOrder = new BankVirtualAccountOrder();
        bankVirtualAccountOrder.setId(bankVirtualAccountOrderDTO.getId());
        bankVirtualAccountOrder.setOrderStatus(bankVirtualAccountOrderDTO.getOrderStatus());
        bankVirtualAccountOrderService.update(bankVirtualAccountOrder);

        BankVirtualAccount queryParam = new BankVirtualAccount();
        queryParam.setClientId(clientId);
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.queryListByClient(queryParam);
        for (BankVirtualAccount virtualAccount : bankVirtualAccounts) {
            BankVirtualAccount account = new BankVirtualAccount();
            account.setVirtualAccountNo(virtualAccount.getVirtualAccountNo());
            bankVirtualAccountService.statisticsAmount(account);
        }
    }

    @Override
    public void saveOrdersAndUpdateAccount(List<BankVirtualAccountOrderDTO> vAOrderList, String clientId) {
        List<BankVirtualAccountOrder> accountOrders = BeanMapperUtils.mapList(vAOrderList, BankVirtualAccountOrder.class);
        bankVirtualAccountOrderService.saveOrdersAndUpdateAccount(accountOrders, clientId);
    }

    @Override
    public List<BankVirtualAccountResDTO> queryListBankVirtualAccount(BankVirtualAccountDTO bankVirtualAccountDTO) {
        BankVirtualAccount virtualAccount = BeanMapperUtils.map(bankVirtualAccountDTO, BankVirtualAccount.class);
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.queryListBankVirtualAccount(virtualAccount);
        if (CollectionUtils.isEmpty(bankVirtualAccounts)) {
            return null;
        }
        return BeanMapperUtils.mapList(bankVirtualAccounts, BankVirtualAccountResDTO.class);
    }

    @Override
    public List<BankVirtualAccountDailyRecordResDTO> queryBankVirtualAccountDailyRecordList(BankVirtualAccountDailyRecordDTO bankVirtualAccountDailyRecordDTO) {
        BankVirtualAccountDailyRecord virtualAccount = BeanMapperUtils.map(bankVirtualAccountDailyRecordDTO, BankVirtualAccountDailyRecord.class);
        List<BankVirtualAccountDailyRecord> bankVirtualAccountDrs = bankVirtualAccountDailyRecordService.queryByTime(virtualAccount);
        if (CollectionUtils.isEmpty(bankVirtualAccountDrs)) {
            return null;
        }
        return BeanMapperUtils.mapList(bankVirtualAccountDrs, BankVirtualAccountDailyRecordResDTO.class);
    }

    @Override
    public BankVirtualAccountOrderResDTO queryById(Long id) {
        BankVirtualAccountOrder bankVirtualAccountOrder = bankVirtualAccountOrderService.queryVAOrderById(id);
        if (null == bankVirtualAccountOrder) {
            return null;
        }
        return BeanMapperUtils.map(bankVirtualAccountOrder, BankVirtualAccountOrderResDTO.class);
    }

    @Override
    public void updateVAOrder(BankVirtualAccountOrderDTO virtualAccountOrderDTO) {
        BankVirtualAccountOrder bankVirtualAccountOrder = new BankVirtualAccountOrder();
        bankVirtualAccountOrder.setId(virtualAccountOrderDTO.getId());
        bankVirtualAccountOrder.setOrderStatus(virtualAccountOrderDTO.getOrderStatus());
        bankVirtualAccountOrder.setCashAmount(virtualAccountOrderDTO.getCashAmount());
        bankVirtualAccountOrderService.update(bankVirtualAccountOrder);
    }

    @Override
    public RpcMessage<List<BankVirtualAccountOrderResDTO>> listBankVirtualAccountOrders(BankVirtualAccountOrderDTO dto) {
        log.info("查询虚拟账户订单请求参数,dto：{}", JSON.toJSONString(dto));
        BankVirtualAccountOrder order = new BankVirtualAccountOrder();
        order.setCurrency(dto.getCurrency());
        order.setReferenceCode(dto.getReferenceCode());
        order.setOrderStatus(dto.getOrderStatus());
        order.setActionTypes(dto.getActionTypes());
        order.setBankOrderNo(dto.getBankOrderNo());
        order.setOperatorType(dto.getOperatorType());
        order.setRedeemApplyId(dto.getRedeemApplyId());
        order.setActionType(dto.getActionType());
        order.setEndCreateTime(dto.getStartCreateTime());
        order.setStartCreateTime(dto.getEndCreateTime());
        order.setVirtualAccountNoList(dto.getVirtualAccountNoList());
        order.setStartTradeTime(dto.getStartTradeTime());
        order.setEndTradeTime(dto.getEndTradeTime());
        List<BankVirtualAccountOrder> bankVirtualAccountOrders = bankVirtualAccountOrderService.listBankVirtualAccountOrders(order);
        if (CollectionUtils.isEmpty(bankVirtualAccountOrders)) {
            return RpcMessage.success(null);
        }
        List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOS = BeanMapperUtils.mapList(bankVirtualAccountOrders, BankVirtualAccountOrderResDTO.class);
        log.info("查询虚拟账户订单请求返回结果,bankVirtualAccountOrderResDTOS：{}", JSON.toJSONString(bankVirtualAccountOrderResDTOS));
        return RpcMessage.success(bankVirtualAccountOrderResDTOS);
    }

    @Override
    public RpcMessage<Page<BankVirtualAccountOrderResDTO>> queryBankVirtualAccountOrderPage(BankVirtualAccountOrderDTO dto) {
        BankVirtualAccountOrder order = new BankVirtualAccountOrder();
        order.setCurrency(dto.getCurrency());
        order.setReferenceCode(dto.getReferenceCode());
        order.setOrderStatus(dto.getOrderStatus());
        order.setActionTypes(dto.getActionTypes());
        order.setBankOrderNo(dto.getBankOrderNo());
        order.setOperatorType(dto.getOperatorType());
        order.setRedeemApplyId(dto.getRedeemApplyId());
        order.setActionType(dto.getActionType());
        order.setEndCreateTime(dto.getStartCreateTime());
        order.setStartCreateTime(dto.getEndCreateTime());
        order.setVirtualAccountNoList(dto.getVirtualAccountNoList());
        order.setStartTradeTime(dto.getStartTradeTime());
        order.setEndTradeTime(dto.getEndTradeTime());

        Page<BankVirtualAccountOrder> rowBounds = new Page<>(dto.getPageNo(), dto.getPageSize());
        Page<BankVirtualAccountOrder> bankVirtualAccountOrders = bankVirtualAccountOrderService.listBankVirtualAccountOrderPage(order, rowBounds);
        List<BankVirtualAccountOrder> bankVirtualAccountOrderResDTOS = bankVirtualAccountOrders.getRecords();

        Page<BankVirtualAccountOrderResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(bankVirtualAccountOrders, paginationRes.getClass());
        List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOList
                = BeanMapperUtils.mapList(bankVirtualAccountOrderResDTOS, BankVirtualAccountOrderResDTO.class);
        paginationRes.setRecords(bankVirtualAccountOrderResDTOList);

        return RpcMessage.success(paginationRes);
    }

    @Override
    public void handelUobExchangeCallBack(UobExchangeDTO uobExchangeDTO) {
        uobRechargeService.handelUobExchangeCallBack(uobExchangeDTO);
    }

    @Override
    public RpcMessage<UserGoalInfoResDTO> getUserGoalInfo(UserGoalInfoDTO userGoalInfoDTO) {
        log.info("查询userGoal信息，userGoalInfoDTO:{}", JSON.toJSONString(userGoalInfoDTO));
        UserGoalInfoPO userGoalInfoParam = new UserGoalInfoPO();
        userGoalInfoParam.setReferenceCode(userGoalInfoDTO.getReferenceCode());
        userGoalInfoParam.setGoalId(userGoalInfoDTO.getGoalId());
        userGoalInfoParam.setClientId(userGoalInfoDTO.getClientId());
        UserGoalInfoPO userGoalInfoPO = userGoalInfoInfoService.queryUserGoalInfo(userGoalInfoParam);
        if (null == userGoalInfoPO) {
            throw new BusinessException("不存在的goalInfo");
        }
        UserGoalInfoResDTO userGoalInfoResDTO = BeanMapperUtils.map(userGoalInfoPO, UserGoalInfoResDTO.class);
        log.info("查询userGoal信息，userGoalInfoResDTO:{}", JSON.toJSONString(userGoalInfoResDTO));
        return RpcMessage.success(userGoalInfoResDTO);
    }

    //Added By WooiTatt
    @Override
    public RpcMessage<UserGoalInfoResDTO> getUserGoalInfoForStatement(UserGoalInfoDTO userGoalInfoDTO) {
        log.info("查询userGoal信息，userGoalInfoDTO:{}", JSON.toJSONString(userGoalInfoDTO));
        UserGoalInfoPO userGoalInfoParam = new UserGoalInfoPO();
        userGoalInfoParam.setReferenceCode(userGoalInfoDTO.getReferenceCode());
        userGoalInfoParam.setGoalId(userGoalInfoDTO.getGoalId());
        userGoalInfoParam.setClientId(userGoalInfoDTO.getClientId());
        UserGoalInfoPO userGoalInfoPO = userGoalInfoInfoService.queryUserGoalInfoForStatement(userGoalInfoParam);
        if (null == userGoalInfoPO) {
            throw new BusinessException("不存在的goalInfo");
        }
        UserGoalInfoResDTO userGoalInfoResDTO = BeanMapperUtils.map(userGoalInfoPO, UserGoalInfoResDTO.class);
        log.info("查询userGoal信息，userGoalInfoResDTO:{}", JSON.toJSONString(userGoalInfoResDTO));
        return RpcMessage.success(userGoalInfoResDTO);
    }

    @Override
    public RpcMessage<Page<UserGoalInfoResDTO>> getUserGoalInfoPage(UserGoalInfoDTO userGoalInfoDTO) {
        Page<UserGoalInfoPO> rowBounds = new Page<>(
                userGoalInfoDTO.getPageNo(), userGoalInfoDTO.getPageSize());

        UserGoalInfoPO userGoalInfoQuery = new UserGoalInfoPO();
        userGoalInfoQuery = BeanMapperUtils.map(userGoalInfoDTO, UserGoalInfoPO.class);

        Page<UserGoalInfoPO> poPagination = userGoalInfoInfoService.queryPageList(userGoalInfoQuery, rowBounds);

        Page<UserGoalInfoResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(poPagination, paginationRes.getClass());

        List<UserGoalInfoPO> userGoalInfoPOList = poPagination.getRecords();
        List<UserGoalInfoResDTO> userGoalInfoResDTOList = BeanMapperUtils.mapList(userGoalInfoPOList, UserGoalInfoResDTO.class);
        paginationRes.setRecords(userGoalInfoResDTOList);

        return RpcMessage.success(paginationRes);
    }

    @Override
    public RpcMessage<List<UserGoalInfoResDTO>> getUserGoalInfoList(UserGoalInfoDTO userGoalInfoDTO) {
        UserGoalInfoPO userGoalInfoPO = new UserGoalInfoPO();
        userGoalInfoPO.setClientId(userGoalInfoDTO.getClientId());
        userGoalInfoPO.setGoalId(userGoalInfoDTO.getGoalId());
        userGoalInfoPO.setLikeGoalId(userGoalInfoDTO.getLikeGoalId());
        List<UserGoalInfoPO> userGoalInfoPOs = userGoalInfoInfoService.queryUserGoalInfos(userGoalInfoPO);

        List<UserGoalInfoResDTO> userGoalInfoResDTOList = BeanMapperUtils.mapList(userGoalInfoPOs, UserGoalInfoResDTO.class);

        return RpcMessage.success(userGoalInfoResDTOList);
    }

    @Override
    public RpcMessage<BankVirtualAccountOrderResDTO> queryFirstBVAOrder(String virtualAccountNo) {
        BankVirtualAccountOrder bankVirtualAccountOrder = bankVirtualAccountOrderService.queryFirstBVAOrder(virtualAccountNo);
        BankVirtualAccountOrderResDTO resDTO = BeanMapperUtils.map(bankVirtualAccountOrder, BankVirtualAccountOrderResDTO.class);
        return RpcMessage.success(resDTO);
    }

    @Override
    public RpcMessage<List<UserInfoResDTO>> queryUserInfos(UserInfoDTO userInfoDTO) {
        UserInfo userInfoQuery = new UserInfo();
        userInfoQuery.setClientId(userInfoDTO.getClientId());
        userInfoQuery.setClientName(userInfoDTO.getClientName());
        List<UserInfo> userInfo = userInfoService.queryList(userInfoQuery);
        if (null == userInfo) {
            RpcMessage.error("不存在的用户");
        }
        List<UserInfoResDTO> userInfoResDTO = BeanMapperUtils.mapList(userInfo, UserInfoResDTO.class);
        return RpcMessage.success(userInfoResDTO);
    }

    @Override
    public RpcMessage<Page<UserInfoResDTO>> queryUserInfoPage(UserInfoDTO userInfoDTO) {
        Page<UserInfoDTO> rowBounds = new Page<>(
                userInfoDTO.getPageNo(), userInfoDTO.getPageSize());

        Page<UserInfoDTO> poPagination = userInfoService.listUserInfoPage(rowBounds, userInfoDTO);

        Page<UserInfoResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(poPagination, paginationRes.getClass());

        List<UserInfoDTO> userInfoList = poPagination.getRecords();
        List<UserInfoResDTO> userInfoResDTOList = BeanMapperUtils.mapList(userInfoList, UserInfoResDTO.class);
        paginationRes.setRecords(userInfoResDTOList);
        return RpcMessage.success(paginationRes);
    }

    @Override
    public void UobRechargeSyncJobImpl() {
        log.info("#######同步UOB线下入金到松鼠虚拟账户,开始。");
        try {
            uobRechargeService.syncUobRechargeToVirtualAccount();
        } catch (Exception ex) {
            log.error("#######同步UOB线下入金到松鼠虚拟账户异常：", ex);
        }
        log.info("#######同步UOB线下入金到松鼠虚拟账户,完成。");
    }

    @Override
    public List<BankVirtualAccountOrderBalDTO> getListByTradeTime(BankVirtualAccountOrderDTO params) {
        BankVirtualAccountOrder bankVirtualAccountOrderQuery = new BankVirtualAccountOrder();
        BeanMapperUtils.copy(params, bankVirtualAccountOrderQuery);
        List<BankVirtualAccountOrder> bankVirtualAccountOrders = bankVirtualAccountOrderService.getListByTradeTime(bankVirtualAccountOrderQuery);
        if (DataUtil.isEmpty(bankVirtualAccountOrders)) {
            return Lists.newArrayList();
        }
        List<BankVirtualAccountOrderBalDTO> bankVirtualAccountOrderBalDTOS = Lists.newArrayList();
        bankVirtualAccountOrders.stream().forEach(input -> {
            BankVirtualAccountOrderBalDTO bankVirtualAccountOrderBalDTO = new BankVirtualAccountOrderBalDTO();
            bankVirtualAccountOrderBalDTOS.add(bankVirtualAccountOrderBalDTO);
            bankVirtualAccountOrderBalDTO.setVirtualAccountNo(input.getVirtualAccountNo());
            bankVirtualAccountOrderBalDTO.setCashAmount(input.getCashAmount());
            bankVirtualAccountOrderBalDTO.setCurrency(input.getCurrency().getCode());
            String orderStatus = null;
            if (input.getOrderStatus().getValue().equals(VAOrderTradeStatusEnum.HANDLING.getValue())) {
                orderStatus = VAOrderTradeStatusEnum.HANDLING.toString();
            }
            if (input.getOrderStatus().getValue().equals(VAOrderTradeStatusEnum.FAIL.getValue())) {
                orderStatus = VAOrderTradeStatusEnum.FAIL.toString();
            }
            if (input.getOrderStatus().getValue().equals(VAOrderTradeStatusEnum.SUCCESS.getValue())) {
                orderStatus = VAOrderTradeStatusEnum.SUCCESS.toString();
            }
            bankVirtualAccountOrderBalDTO.setOrderStatus(orderStatus);
            bankVirtualAccountOrderBalDTO.setOperatorType(input.getOperatorType().getValue().equals(VAOrderTradeTypeEnum.COME_INTO.getValue()) ? VAOrderTradeTypeEnum.COME_INTO.toString() : VAOrderTradeTypeEnum.COME_OUT.toString());
            bankVirtualAccountOrderBalDTO.setNeedRefundType(input.getNeedRefundType().getValue().equals(NeedRefundTypeEnum.REFUND.getValue()) ? NeedRefundTypeEnum.REFUND.toString() : NeedRefundTypeEnum.UN_REFUND.toString());
            bankVirtualAccountOrderBalDTO.setBankOrderNo(input.getBankOrderNo());
            bankVirtualAccountOrderBalDTO.setTradeTime(DateUtils.formatDate(input.getTradeTime(), DateUtils.DATE_TIME_FORMAT));
            bankVirtualAccountOrderBalDTO.setReferenceCode(input.getReferenceCode());
            bankVirtualAccountOrderBalDTO.setRedeemApplyId(input.getRedeemApplyId());
            String actionType = null;
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.REDEEM_EXCHANGE.getValue())) {
                actionType = VAOrderActionTypeEnum.REDEEM_EXCHANGE.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.RECHARGE_EXCHANGE.getValue())) {
                actionType = VAOrderActionTypeEnum.RECHARGE_EXCHANGE.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.REDEEM.getValue())) {
                actionType = VAOrderActionTypeEnum.REDEEM.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.RECHARGE.getValue())) {
                actionType = VAOrderActionTypeEnum.RECHARGE.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.REFUND.getValue())) {
                actionType = VAOrderActionTypeEnum.REFUND.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.UOBTOSAXO.getValue())) {
                actionType = VAOrderActionTypeEnum.UOBTOSAXO.toString();
            }
            if (input.getActionType().getValue().equals(VAOrderActionTypeEnum.SAXOTOUOB.getValue())) {
                actionType = VAOrderActionTypeEnum.SAXOTOUOB.toString();
            }
            bankVirtualAccountOrderBalDTO.setActionType(actionType);
        });
        return bankVirtualAccountOrderBalDTOS;
    }

    @Override
    public List<BankVirtualAccountOrderResDTO> getByTradeTime(BankVirtualAccountOrderDTO params) {
        BankVirtualAccountOrder bankVirtualAccountOrderQuery = new BankVirtualAccountOrder();
        BeanMapperUtils.copy(params, bankVirtualAccountOrderQuery);
        List<BankVirtualAccountOrder> bankVirtualAccountOrders = bankVirtualAccountOrderService.getListByTradeTime(bankVirtualAccountOrderQuery);
        return BeanMapperUtils.mapList(bankVirtualAccountOrders, BankVirtualAccountOrderResDTO.class);
    }

    @Override
    public List<BankVirtualAccountBalDTO> getAccountBalByTradeTime(Map<String, Object> params) {
        List<BankVirtualAccount> bankVirtualAccounts = bankVirtualAccountService.getListByTradeTime(params);

        if (DataUtil.isEmpty(bankVirtualAccounts)) {
            return Lists.newArrayList();
        }
        List<BankVirtualAccountBalDTO> bankVirtualAccountBalDTOS = Lists.newArrayList();
        bankVirtualAccounts.stream().forEach(input -> {
            BankVirtualAccountBalDTO bankVirtualAccountBalDTO = new BankVirtualAccountBalDTO();
            bankVirtualAccountBalDTOS.add(bankVirtualAccountBalDTO);
            bankVirtualAccountBalDTO.setClientId(input.getClientId());
            bankVirtualAccountBalDTO.setClientName(input.getClientName());
            bankVirtualAccountBalDTO.setVirtualAccountNo(input.getVirtualAccountNo());
            bankVirtualAccountBalDTO.setCashAmount(input.getCashAmount());
            bankVirtualAccountBalDTO.setFreezeAmount(input.getFreezeAmount());
            bankVirtualAccountBalDTO.setCreateTime(DateUtils.formatDate(input.getCreateTime(), DateUtils.DATE_TIME_FORMAT));
            bankVirtualAccountBalDTO.setCurrency(input.getCurrency().getCode());
            bankVirtualAccountBalDTO.setUsedAmount(input.getUsedAmount());
        });
        return bankVirtualAccountBalDTOS;
    }

  /*  @Override
    public void staticBankVirtualAccountJob() {
        staticBankVirtualAccountJob.execute(null);
    }*/

    @Override
    public void fixBankOrder() {
        fixBankOrder.fixBankOrder();
    }

    @Override
    public void updateDeletedByClientIdAndGoalId(String clientId, String goalId) {
        userGoalInfoInfoService.updateDeletedByClientIdAndGoalId(clientId, goalId);
    }
    
    //Added By WooiTatt
    @Override
    public void updateUserRechargeStatus(Long getId, Long setId, UserRechargeStatusEnum status) {
        UserRechargeStatus userRechargeStatus = new UserRechargeStatus();
        if(status.equals(UserRechargeStatusEnum.PROGRESSUOBTOSAXO)){
            userRechargeStatus.setSaxoAccOrderId(setId.toString());
            userRechargeStatus.setBankVirtualAccOrderId(getId.toString());
            userRechargeStatus.setUserRechargeStatusEnum(UserRechargeStatusEnum.PROGRESSUOBTOSAXO);
            userRechargeStatusService.updateUserRechargeStatus(userRechargeStatus);
        }
        if(status.equals(UserRechargeStatusEnum.INTERTRANSTOUSD)){
            userRechargeStatus.setAccRechargeId(setId.toString());
            userRechargeStatus.setSaxoAccOrderId(getId.toString());
            userRechargeStatus.setUserRechargeStatusEnum(UserRechargeStatusEnum.INTERTRANSTOUSD);
            userRechargeStatusService.updateUserRechargeStatusBySaxoOrderAccId(userRechargeStatus);
        }
        
    }
    
    @Override
    public void updateUserRechargeStatusToSuccess(Long accountRechargeId) {
        UserRechargeStatus userRechargeStatus = new UserRechargeStatus();
        userRechargeStatus.setAccRechargeId(accountRechargeId.toString());
        userRechargeStatus.setUserRechargeStatusEnum(UserRechargeStatusEnum.SUCCESS);
        userRechargeStatusService.updateUserRechargeStatusToSuccess(userRechargeStatus);
    }
    
    @Override
    public BigDecimal getPendingDeposit(String clientId, String goalId) {
        UserRechargeStatus userRechargeStatus = new UserRechargeStatus();
        userRechargeStatus.setClientId(clientId);
        userRechargeStatus.setGoalId(goalId);
        userRechargeStatus.setUserRechargeStatusEnum(UserRechargeStatusEnum.SUCCESS);
        return userRechargeStatusService.getPendingDeposit(userRechargeStatus);
    }

}
