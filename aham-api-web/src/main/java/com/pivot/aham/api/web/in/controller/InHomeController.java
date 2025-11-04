package com.pivot.aham.api.web.in.controller;


import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.req.UserProfitInfoReqDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.api.server.remoteservice.AccountRechargeRemoteService;
import com.pivot.aham.api.server.remoteservice.AccountRedeemRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoAccountOrderRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.UserStaticsRemoteService;
import com.pivot.aham.api.web.in.vo.HomeStaticsResVo;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;



@RestController
@RequestMapping("/api/v1/in")
@Api(value = "首页接口", description = "首页接口")
public class InHomeController {
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private UserStaticsRemoteService userStaticsRemoteService;
    @Resource
    private SaxoAccountOrderRemoteService saxoAccountOrderRemoteService;
    @Resource
    private AccountRechargeRemoteService accountRechargeRemoteService;
    @Resource
    private AccountRedeemRemoteService accountRedeemRemoteService;
   
    @ApiOperation(value = "获取首页统计信息")
    @PostMapping("/home/statics")
    @RequiresPermissions("in:home:read")
    public Message<HomeStaticsResVo> statics() {
        HomeStaticsResVo homeStaticsResVo = new HomeStaticsResVo();

        UserInfoDTO userInfoDTO = new UserInfoDTO();
        RpcMessage<List<UserInfoResDTO>> rpcMessage = userServiceRemoteService.queryUserInfos(userInfoDTO);
        if(rpcMessage.isSuccess()){
            List<UserInfoResDTO> resList = rpcMessage.getContent();
            homeStaticsResVo.setNumOfUsers(resList.size());
        }

        BigDecimal investmentSgd = BigDecimal.ZERO;
        UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
        userStaticsReqDTO.setStaticDate(CalDateSupport.getCalYesDate());
        RpcMessage<List<UserStaticsResDTO>> rpcMessageStatics = userStaticsRemoteService.getUserStatics(userStaticsReqDTO);
        if(rpcMessageStatics.isSuccess()){
            BigDecimal totalAdjFundAsset = BigDecimal.ZERO;
            for(UserStaticsResDTO userStaticsRes:rpcMessageStatics.getContent()){
                totalAdjFundAsset = totalAdjFundAsset.add(userStaticsRes.getAdjFundAssetInSgd());
            }

            investmentSgd = investmentSgd.add(totalAdjFundAsset);
        }
        homeStaticsResVo.setTotalInvestmentSgd(convertNumberFormat(investmentSgd));
        
        RpcMessage<BigDecimal> rpcMessageAccountRecharge =
                accountRechargeRemoteService.getSumAccountRecharge();

     /*   SaxoAccountOrderReqDTO saxoAccountOrderReqQueryForDeposit = new SaxoAccountOrderReqDTO();
        saxoAccountOrderReqQueryForDeposit.setCurrency(CurrencyEnum.SGD);
        saxoAccountOrderReqQueryForDeposit.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
        saxoAccountOrderReqQueryForDeposit.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
        saxoAccountOrderReqQueryForDeposit.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        RpcMessage<List<SaxoAccountOrderResDTO>> rpcMessageSaxoAccountOrders_deposit =
                saxoAccountOrderRemoteService.getSaxoAccountOrders(saxoAccountOrderReqQueryForDeposit);
        BigDecimal totalDeposit = BigDecimal.ZERO;
        if(rpcMessageSaxoAccountOrders_deposit.isSuccess()){

            List<SaxoAccountOrderResDTO> saxoAccountOrderResDTOList = rpcMessageSaxoAccountOrders_deposit.getContent();
            for(SaxoAccountOrderResDTO saxoAccountOrderResDTO:saxoAccountOrderResDTOList){
                totalDeposit = totalDeposit.add(saxoAccountOrderResDTO.getCashAmount());
            }
        }*/
      if(rpcMessageAccountRecharge.isSuccess()){
        homeStaticsResVo.setTotalDepositSgd(convertNumberFormat(rpcMessageAccountRecharge.getContent()));
      }

       RpcMessage<BigDecimal> rpcMessageAccountRedeem =
                accountRedeemRemoteService.getSumRedeemConfirmAmount();

        /*SaxoAccountOrderReqDTO saxoAccountOrderReqQueryForWithdrawal = new SaxoAccountOrderReqDTO();
        saxoAccountOrderReqQueryForWithdrawal.setCurrency(CurrencyEnum.SGD);
        saxoAccountOrderReqQueryForWithdrawal.setActionType(SaxoOrderActionTypeEnum.SAXOTOUOB);
        saxoAccountOrderReqQueryForWithdrawal.setOperatorType(SaxoOrderTradeTypeEnum.COME_OUT);
        saxoAccountOrderReqQueryForWithdrawal.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
        RpcMessage<List<SaxoAccountOrderResDTO>> rpcMessageSaxoAccountOrders_withdrawal =
                saxoAccountOrderRemoteService.getSaxoAccountOrders(saxoAccountOrderReqQueryForWithdrawal);
        BigDecimal totalWithdrawal = BigDecimal.ZERO;
        if(rpcMessageSaxoAccountOrders_withdrawal.isSuccess()){
            List<SaxoAccountOrderResDTO> saxoAccountOrderResDTOList = rpcMessageSaxoAccountOrders_withdrawal.getContent();
            for(SaxoAccountOrderResDTO saxoAccountOrderResDTO:saxoAccountOrderResDTOList){
                totalWithdrawal = totalWithdrawal.add(saxoAccountOrderResDTO.getCashAmount());
            }

        }*/
        if(rpcMessageAccountRedeem.isSuccess()){
            homeStaticsResVo.setTotalWithdrawalSgd(convertNumberFormat(rpcMessageAccountRedeem.getContent()));
        }


       /* BigDecimal totalSquirrelCashSgd = BigDecimal.ZERO;
        BigDecimal totalSquirrelCashUsd = BigDecimal.ZERO;
        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
        List<BankVirtualAccountResDTO> bankVirtualAccountResDTOList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);
        for(BankVirtualAccountResDTO bankVirtualAccountResDTO:bankVirtualAccountResDTOList){
            if(bankVirtualAccountResDTO.getCurrency() == CurrencyEnum.SGD){
                totalSquirrelCashSgd = totalSquirrelCashSgd.add(bankVirtualAccountResDTO.getCashAmount());
            }else{
                totalSquirrelCashUsd = totalSquirrelCashUsd.add(bankVirtualAccountResDTO.getCashAmount());
            }
        }
        homeStaticsResVo.setTotalSquirrelCashSgd(totalSquirrelCashSgd);
        homeStaticsResVo.setTotalSquirrelCashUsd(totalSquirrelCashUsd);
*/


       // BigDecimal totalFxImpact = BigDecimal.ZERO;
        BigDecimal portfolioReturn = BigDecimal.ZERO;
        BigDecimal totalReturn = BigDecimal.ZERO;
        UserProfitInfoReqDTO userProfitInfoReqDTO = new UserProfitInfoReqDTO();
        userProfitInfoReqDTO.setProfitDate(CalDateSupport.getCalDate());
        RpcMessage<List<UserProfitInfoResDTO>> rpcProfitInfo =
                userStaticsRemoteService.getUserProfitInfos(userProfitInfoReqDTO);
        if(rpcProfitInfo.isSuccess()){
            List<UserProfitInfoResDTO> userProfitInfoResDTOList = rpcProfitInfo.getContent();
            for(UserProfitInfoResDTO userProfitInfoResDTO:userProfitInfoResDTOList){
               // totalFxImpact = totalFxImpact.add(userProfitInfoResDTO.getFxImpact());
                portfolioReturn = portfolioReturn.add(userProfitInfoResDTO.getPortfolioProfit());
                totalReturn = totalReturn.add(userProfitInfoResDTO.getTotalProfit());
            }

        }
        //homeStaticsResVo.setFxImpact(totalFxImpact);
        homeStaticsResVo.setPortfolioReturn(convertNumberFormat(portfolioReturn));
        homeStaticsResVo.setTotalReturn(convertNumberFormat(totalReturn));



//        homeStaticsResVo.setFxImpact(new BigDecimal("0.08383"));
//        homeStaticsResVo.setNumOfUsers(12L);
//        homeStaticsResVo.setPortfolioReturn(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalDepositSgd(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalInvestmentSgd(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalReturn(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalSquirrelCashSgd(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalSquirrelCashUsd(new BigDecimal("0.08383"));
//        homeStaticsResVo.setTotalWithdrawalSgd(new BigDecimal("0.08383"));
        homeStaticsResVo.setUpdateTime(DateUtils.now());

        CalDecimal<HomeStaticsResVo> calDecimal = new CalDecimal<>();
        calDecimal.handleDot(homeStaticsResVo);

        return Message.success(homeStaticsResVo);
    }
    
    private String convertNumberFormat(BigDecimal number){
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(number);
    }
}
