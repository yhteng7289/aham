package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.server.dto.req.UserGoalOrderDTO;
import com.pivot.aham.api.server.dto.res.UserGoalOrderResDTO;
import com.pivot.aham.api.server.remoteservice.OrderServiceRemoteService;
import com.pivot.aham.api.service.mapper.model.AccountRechargePO;
import com.pivot.aham.api.service.mapper.model.AccountRedeemPO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.RedeemApplyPO;
import com.pivot.aham.api.service.service.AccountRechargeService;
import com.pivot.aham.api.service.service.AccountRedeemService;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.api.service.service.RedeemApplyService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.TransactionOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RechargeOrderStatusEnum;
import com.pivot.aham.common.enums.analysis.RedeemApplyStatusEnum;
import com.pivot.aham.common.enums.analysis.SaxoToUobTransferStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import com.pivot.aham.common.enums.recharge.TncfStatusEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 19/3/5.
 */
@Service(interfaceClass = OrderServiceRemoteService.class)
@Slf4j
public class OrderServiceRemoteServiceImpl implements OrderServiceRemoteService {

    @Resource
    private AccountRechargeService accountRechargeService;

    @Resource
    private AccountUserService accountUserService;

    @Resource
    private AccountRedeemService accountRedeemService;
    
    @Resource
    private RedeemApplyService redeemApplyService;

    @Override
    public RpcMessage<List<UserGoalOrderResDTO>> queryUserGoalOrders(UserGoalOrderDTO userGoalOrderDTO) {
        List<UserGoalOrderResDTO> userGoalOrderResDTOs = Lists.newArrayList();
        //查询对应的portfolio下的goal的充值和提现流水
        AccountUserPO accountUserParam = new AccountUserPO();
        accountUserParam.setGoalId(userGoalOrderDTO.getGoalId());
        accountUserParam.setClientId(userGoalOrderDTO.getClientId());
        AccountUserPO accountUserPO = accountUserService.queryAccountUser(accountUserParam);

        AccountRechargePO accountRechargeParam = new AccountRechargePO();
        accountRechargeParam.setAccountId(accountUserPO.getAccountId());
        accountRechargeParam.setClientId(accountUserPO.getClientId());
        accountRechargeParam.setGoalId(accountUserPO.getGoalId());
        List<AccountRechargePO> accountRechargePOs = accountRechargeService.listAccountRecharge(accountRechargeParam);
        for (AccountRechargePO po : accountRechargePOs) {
            UserGoalOrderResDTO resDTO = new UserGoalOrderResDTO();
            resDTO.setGoalId(po.getGoalId());
            resDTO.setClientId(po.getClientId());
            resDTO.setOrderStatus(getRechargeTransactionOrderStatus(po.getOrderStatus()));
            resDTO.setMoney(po.getRechargeAmount());
            resDTO.setOrderNo(po.getRechargeOrderNo());
            resDTO.setOrderTime(DateUtils.formatDate(po.getRechargeTime(), DateUtils.DATE_TIME_FORMAT));
            resDTO.setOrderType(VAOrderTradeTypeEnum.COME_INTO.getValue());
            userGoalOrderResDTOs.add(resDTO);
        }

        AccountRedeemPO accountRedeemParam = new AccountRedeemPO();
        accountRedeemParam.setAccountId(accountUserPO.getAccountId());
        accountRedeemParam.setClientId(accountUserPO.getClientId());
        accountRedeemParam.setGoalId(accountUserPO.getGoalId());
        List<AccountRedeemPO> accountRedeemPOs = accountRedeemService.listAccountRedeem(accountRedeemParam);
        for (AccountRedeemPO po : accountRedeemPOs) {
            UserGoalOrderResDTO resDTO = new UserGoalOrderResDTO();
            resDTO.setGoalId(po.getGoalId());
            resDTO.setClientId(po.getClientId());
            resDTO.setOrderStatus(getRedeemTransactionOrderStatus(po.getTncfStatus()));
            // Process Success //
            if (resDTO.getOrderStatus() == 2) {
                resDTO.setMoney(po.getConfirmMoney());
            } else {
                resDTO.setMoney(po.getApplyMoney());
            }

            resDTO.setOrderNo(po.getId());
            resDTO.setOrderTime(DateUtils.formatDate(po.getRedeemApplyTime(), DateUtils.DATE_TIME_FORMAT));
            resDTO.setOrderType(VAOrderTradeTypeEnum.COME_OUT.getValue());
            userGoalOrderResDTOs.add(resDTO);
        }

        return RpcMessage.success(userGoalOrderResDTOs);
    }

    private Integer getRedeemTransactionOrderStatus(TncfStatusEnum orderStatus) {
        Integer orderStatusValue = 0;
        switch (orderStatus) {
            case PROCESSING:
            case TNCF:
                orderStatusValue = TransactionOrderStatusEnum.PROCESSING.getValue();
                break;
            case SUCCESS:
                orderStatusValue = TransactionOrderStatusEnum.SUCCESS.getValue();
                break;
            case FAIL:
                orderStatusValue = TransactionOrderStatusEnum.FAIL.getValue();
                break;
        }
        return orderStatusValue;
    }

    private Integer getRechargeTransactionOrderStatus(RechargeOrderStatusEnum orderStatus) {
        Integer orderStatusValue = 0;
        switch (orderStatus) {
            case PROCESSING:
                orderStatusValue = TransactionOrderStatusEnum.PROCESSING.getValue();
                break;
            case SUCCESS:
                orderStatusValue = TransactionOrderStatusEnum.SUCCESS.getValue();
                break;
            case FAIL:
                orderStatusValue = TransactionOrderStatusEnum.FAIL.getValue();
                break;
        }
        return orderStatusValue;
    }

	@Override
	public RpcMessage<JSONArray> findRedeemApplyPage(Date startConfirmTime, Date endConfirmTime) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.info("startConfirmTime:{}", startConfirmTime);
		log.info("endCreateTime:{}", endConfirmTime);
		JSONArray result = new JSONArray();
		RedeemApplyPO query = new RedeemApplyPO();
		query.setStartConfirmTime(startConfirmTime);
		query.setEndConfirmTime(endConfirmTime);
		query.setRedeemApplyStatus(RedeemApplyStatusEnum.SUCCESS);
		query.setSaxoToUobTransferStatus(SaxoToUobTransferStatusEnum.APPLYSUCCESS);
		log.info("==query:{}", JSON.toJSONString(query));
		List<RedeemApplyPO> redeemApplyList = redeemApplyService.listRedeemApply(query);
		log.info("==redeemApplyList:{}", JSON.toJSONString(redeemApplyList));
		for(RedeemApplyPO redeemApply : redeemApplyList) {
			if(redeemApply.getSaxoToUobBatchId().isEmpty()) {
				continue;
			}
			Map<String, String> object = new HashMap<String, String>();
			object.put("confirmTime", dateFormat.format(redeemApply.getConfirmTime()));
			object.put("clientId", redeemApply.getClientId());
			object.put("goalId", redeemApply.getGoalId());
			object.put("confirmAmount", redeemApply.getConfirmAmount()+"");
			object.put("saxoToUobBatchId", redeemApply.getSaxoToUobBatchId());
                        object.put("bankName", redeemApply.getBankName());
                        object.put("bankAccountNo", redeemApply.getBankAccountNo());
			result.add(object);
		}
		return RpcMessage.success(result);
	}

}
