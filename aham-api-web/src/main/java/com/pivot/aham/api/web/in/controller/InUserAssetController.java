package com.pivot.aham.api.web.in.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.req.AccountUserReqDTO;
import com.pivot.aham.api.server.remoteservice.*;
import com.pivot.aham.api.server.dto.req.ExchangeRateDTO;
import com.pivot.aham.api.server.dto.req.TransOrderReqDTO;
import com.pivot.aham.api.server.dto.req.UserEtfSharesReqDTO;
import com.pivot.aham.api.server.dto.req.UserProfitInfoReqDTO;
import com.pivot.aham.api.server.dto.res.AccountUserResDTO;
import com.pivot.aham.api.server.dto.res.ExchangeRateResDTO;
import com.pivot.aham.api.server.dto.res.TransOrderResDTO;
import com.pivot.aham.api.server.dto.res.UserEtfSharesResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.api.web.in.vo.*;
import com.pivot.aham.common.enums.analysis.*;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.ExchangeRateTypeEnum;
import com.pivot.aham.common.enums.MatchTypeEnum;
import com.pivot.aham.common.enums.in.TransTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/in")
@Api(value = "用户资产管理", description = "用户资产管理接口")
public class InUserAssetController {
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private UserStaticsRemoteService userStaticsRemoteService;
    @Resource
    private SaxoAccountOrderRemoteService saxoAccountOrderRemoteService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private TransRemoteService transRemoteService;
    @Resource
    private ExchangeRemoteService exchangeRemoteService;
    @Resource
    private AccountRechargeRemoteService accountRechargeRemoteService;
    @Resource
    private AccountRedeemRemoteService accountRedeemRemoteService;
    @Resource
    private AccountUserRemoteService accountUserRemoteService;


    @ApiOperation(value = "获取用户列表(分页)")
    @PostMapping("/userAsset/clientList")
    @RequiresPermissions("in:userAsset:read")
    public Message<Page<ClientInfoResVo>> clientList(@RequestBody ClientInfoReqVo clientInfoReqVo) {
        Page<ClientInfoResVo> paginationClient = new Page<>();
        //分页查询用户
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setLikeClientId(clientInfoReqVo.getClientId());
        userInfoDTO.setLikeClientName(clientInfoReqVo.getClientName());
        userInfoDTO.setPageNo(clientInfoReqVo.getPageNo());
        userInfoDTO.setPageSize(clientInfoReqVo.getPageSize());
        RpcMessage<Page<UserInfoResDTO>> rpcMessage_userInfo = userServiceRemoteService.queryUserInfoPage(userInfoDTO);
        if(rpcMessage_userInfo.isSuccess()){
            Page<UserInfoResDTO> pagination = rpcMessage_userInfo.getContent();


            paginationClient = BeanMapperUtils.map(pagination,paginationClient.getClass());

            List<UserInfoResDTO> userInfoList = pagination.getRecords();
            List<ClientInfoResVo> goalResVoList = Lists.newArrayList();
            for(UserInfoResDTO userInfoResDTO:userInfoList){
                ClientInfoResVo clientInfoResVo = new ClientInfoResVo();
                clientInfoResVo.setClientId(userInfoResDTO.getClientId());
                clientInfoResVo.setClientName(userInfoResDTO.getClientName());
                clientInfoResVo.setRegistrationTime(userInfoResDTO.getCreateTime());
                //查询每个client下的所有goal
                UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
                userGoalInfoDTO.setClientId(userInfoResDTO.getClientId());
                RpcMessage<List<UserGoalInfoResDTO>> rpcMessageUserGoal
                        = userServiceRemoteService.getUserGoalInfoList(userGoalInfoDTO);

                if(rpcMessageUserGoal.isSuccess()){
                    List<UserGoalInfoResDTO> userGoalInfoResDTOList = rpcMessageUserGoal.getContent();
                    clientInfoResVo.setNumOfGoals(userGoalInfoResDTOList.size());
                }

                //查询bankvirtualaccount
                /*BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
                bankVirtualAccountDTO.setClientId(userInfoResDTO.getClientId());
                List<BankVirtualAccountResDTO> bankVirtualAccountList =
                        userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);
                if(bankVirtualAccountList != null) {
                    for (BankVirtualAccountResDTO bankVirtualAccountRes : bankVirtualAccountList) {
                        if (bankVirtualAccountRes.getCurrency() == CurrencyEnum.SGD) {
                            clientInfoResVo.setBankVirtualAccountNoSgd(bankVirtualAccountRes.getVirtualAccountNo());
                            clientInfoResVo.setTotalSquirrelCashSgd(bankVirtualAccountRes.getCashAmount());
                        } else {
                            clientInfoResVo.setBankVirtualAccountNoUsd(bankVirtualAccountRes.getVirtualAccountNo());
                            clientInfoResVo.setTotalSquirrelCashUsd(bankVirtualAccountRes.getCashAmount());
                        }
                    }
                }else{
                    clientInfoResVo.setBankVirtualAccountNoSgd("");
                    clientInfoResVo.setTotalSquirrelCashSgd(BigDecimal.ZERO);
                    clientInfoResVo.setBankVirtualAccountNoUsd("");
                    clientInfoResVo.setTotalSquirrelCashUsd(BigDecimal.ZERO);

                }*/
                
                    clientInfoResVo.setBankVirtualAccountNoSgd("");
                    clientInfoResVo.setTotalSquirrelCashSgd(BigDecimal.ZERO);
                    clientInfoResVo.setBankVirtualAccountNoUsd("");
                    clientInfoResVo.setTotalSquirrelCashUsd(BigDecimal.ZERO);
                
                //查询该用户所有的userstatics
                //查询所有goal的statics ...
                BigDecimal totalAssetSgd = BigDecimal.ZERO;
                BigDecimal totalAssetUsd = BigDecimal.ZERO;
                UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
                userStaticsReqDTO.setClientId(userInfoResDTO.getClientId());
                //Date yesterday = DateUtils.addDateByDay(DateUtils.now(), -3);
                userStaticsReqDTO.setStaticDate(CalDateSupport.getCalYesDate());
                //userStaticsReqDTO.setStaticDate(yesterday);
                RpcMessage<List<UserStaticsResDTO>> rpcMessage_userStatics
                        = userStaticsRemoteService.getUserStatics(userStaticsReqDTO);
                if(rpcMessage_userStatics.isSuccess()){
                    List<UserStaticsResDTO> userStaticsResList = rpcMessage_userStatics.getContent();
                    for(UserStaticsResDTO userStaticsResDTO:userStaticsResList){
                        totalAssetSgd = totalAssetSgd.add(userStaticsResDTO.getAdjFundAssetInSgd());
                        totalAssetUsd = totalAssetUsd.add(userStaticsResDTO.getAdjFundAsset());
                    }
                }

                clientInfoResVo.setTotalInvestmentSgd(totalAssetSgd);
                clientInfoResVo.setTotalInvestmentUsd(totalAssetUsd);

                BigDecimal fxrt2 = BigDecimal.ONE;
                //BigDecimal fxrt2 = BigDecimal.ZERO;
                //ExchangeRateDTO exchangeRateParam = new ExchangeRateDTO();
                //exchangeRateParam.setExchangeRateType(ExchangeRateTypeEnum.SAXO_FXRT2);
                //RpcMessage<ExchangeRateResDTO> rateResDTORpcMessage = exchangeRemoteService.getLastExchangeRate(exchangeRateParam);
                //if(rateResDTORpcMessage.isSuccess()){
                //    ExchangeRateResDTO exchangeRateResDTO = rateResDTORpcMessage.getContent();
                //    fxrt2 = exchangeRateResDTO.getUsdToSgd();
                //}

                BigDecimal totalSquirrelExchangeSgd = BigDecimal.ZERO;//clientInfoResVo.getTotalSquirrelCashUsd().multiply(fxrt2);
                BigDecimal totalSquirrelExchangeUsd = BigDecimal.ZERO;//clientInfoResVo.getTotalSquirrelCashSgd().divide(fxrt2,6,BigDecimal.ROUND_DOWN);

                BigDecimal totalSquirrelSgd = totalSquirrelExchangeSgd.add(clientInfoResVo.getTotalSquirrelCashSgd());
                BigDecimal totalSquirrelUsd = totalSquirrelExchangeUsd.add(clientInfoResVo.getTotalSquirrelCashUsd());

                BigDecimal totalWealthSgd = totalAssetSgd.add(totalSquirrelSgd);
                BigDecimal totalWealthUsd = totalAssetUsd.add(totalSquirrelUsd);
                clientInfoResVo.setTotalWealthSgd(totalWealthSgd);
                clientInfoResVo.setTotalWealthUsd(totalWealthUsd);

                CalDecimal<ClientInfoResVo> clientInfoResVoCalDecimal = new CalDecimal<>();
                clientInfoResVoCalDecimal.handleDot(clientInfoResVo);

                goalResVoList.add(clientInfoResVo);

            }
            paginationClient.setRecords(goalResVoList);

        }


//        Page<ClientInfoResVo> pagination = new Page<>();
//        ClientInfoResVo clientInfoResVo = new ClientInfoResVo();
//        clientInfoResVo.setBankVirtualAccountNoSgd(new BigDecimal("0.0993"));
//        clientInfoResVo.setBankVirtualAccountNoUsd(new BigDecimal("1.0908"));

//        clientInfoResVo.setNumOfGoals(11);
//        clientInfoResVo.setRegistrationTime(DateUtils.now());
//        clientInfoResVo.setTotalInvestmentSgd(new BigDecimal("12.222"));
//        clientInfoResVo.setTotalInvestmentUsd(new BigDecimal("12.222"));
//        clientInfoResVo.setTotalSquirrelCashUsd(new BigDecimal("12.0930"));
//        clientInfoResVo.setTotalSquirrelCashSgd(new BigDecimal("12.0930"));
//        clientInfoResVo.setTotalWealthSgd(new BigDecimal("111.2222"));
//        clientInfoResVo.setTotalWealthUsd(new BigDecimal("22.111"));

//        List<ClientInfoResVo> goalResVoList = Lists.newArrayList();
//        goalResVoList.add(clientInfoResVo);
//        pagination.setRecords(goalResVoList);

        return Message.success(paginationClient);
    }
    @ApiOperation(value = "获取某个client的Goal的List")
    @PostMapping("/userAsset/clientGoalList")
    @RequiresPermissions("in:userAsset:read")
    public Message<ClientGoalResVo> clientGoalList(@RequestBody ClientGoalReqVo clientGoalReqVo) {
        ClientGoalResVo clientGoalRes = new ClientGoalResVo();
        List<ClientGoalResBeanVo> clientGoalResVoList = Lists.newArrayList();
        //查询每个client下的所有goal
        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
        userGoalInfoDTO.setClientId(clientGoalReqVo.getClientId());
        userGoalInfoDTO.setLikeGoalId(clientGoalReqVo.getGoalId());
        RpcMessage<List<UserGoalInfoResDTO>> rpcMessageUserGoal
                = userServiceRemoteService.getUserGoalInfoList(userGoalInfoDTO);

        BigDecimal totalAssetUsd = BigDecimal.ZERO;
        BigDecimal totalAssetSgd = BigDecimal.ZERO;
        if(rpcMessageUserGoal.isSuccess()){
            List<UserGoalInfoResDTO> userGoalInfoResDTOList = rpcMessageUserGoal.getContent();
            for(UserGoalInfoResDTO userGoalInfoResDTO:userGoalInfoResDTOList){
                ClientGoalResBeanVo clientGoalResVo = new ClientGoalResBeanVo();
                clientGoalResVo.setClientId(userGoalInfoResDTO.getClientId());
                clientGoalResVo.setGoalId(userGoalInfoResDTO.getGoalId());
                clientGoalResVo.setPortfolioId(userGoalInfoResDTO.getPortfolioId());
                clientGoalResVo.setCurrency(CurrencyEnum.SGD);
                clientGoalResVo.setCreateTime(userGoalInfoResDTO.getCreateTime());
                clientGoalResVo.setReferenceCode(userGoalInfoResDTO.getReferenceCode());
                
                AccountUserReqDTO accountUserReq = new AccountUserReqDTO();
                accountUserReq.setClientId(userGoalInfoResDTO.getClientId());
                accountUserReq.setGoalId(userGoalInfoResDTO.getGoalId());
                RpcMessage<List<AccountUserResDTO>> rpcMessage = accountUserRemoteService.getAccountUserList(accountUserReq);
                if(rpcMessage.isSuccess()) {
                    List<AccountUserResDTO> accountUserResDTOList = rpcMessage.getContent();
                    if(accountUserResDTOList.size() > 0){
                        clientGoalResVo.setAccountId(accountUserResDTOList.get(0).getAccountId());
                    }
                }
                //查询userstatics
                BigDecimal assetValue = BigDecimal.ZERO;
                BigDecimal assetValueSgd = BigDecimal.ZERO;
                BigDecimal exchangeRate = BigDecimal.ZERO;
                UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
                userStaticsReqDTO.setClientId(userGoalInfoResDTO.getClientId());
                userStaticsReqDTO.setGoalId(userGoalInfoResDTO.getGoalId());
                userStaticsReqDTO.setStaticDate(CalDateSupport.getCalYesDate());
                RpcMessage<List<UserStaticsResDTO>> rpcMessage_userStatics
                        = userStaticsRemoteService.getUserStatics(userStaticsReqDTO);

                if(rpcMessage_userStatics.isSuccess()){
                    List<UserStaticsResDTO> userStaticsResDTOList = rpcMessage_userStatics.getContent();
                    if(userStaticsResDTOList.size()>0) {
                        UserStaticsResDTO userStaticsRes = userStaticsResDTOList.get(0);
                        assetValue = userStaticsRes.getAdjFundAsset();
                        assetValueSgd = userStaticsRes.getAdjFundAssetInSgd();
                        exchangeRate = userStaticsRes.getFxRateForFundOut();
                    }
                }
                totalAssetUsd = totalAssetUsd.add(assetValue);
                totalAssetSgd = totalAssetSgd.add(assetValueSgd);
                clientGoalResVo.setAssetValue(assetValue);
                clientGoalResVo.setAssetValueSgd(assetValueSgd);
                clientGoalResVo.setExchangeRate(exchangeRate);

                //查询某个goal的saxoaccountorder
                AccountRechargeVoDTO accountRechargeVoDTO = new AccountRechargeVoDTO();
                accountRechargeVoDTO.setClientId(userGoalInfoResDTO.getClientId());
                accountRechargeVoDTO.setGoalId(userGoalInfoResDTO.getGoalId());
                RpcMessage<BigDecimal> rpcMessageAccountRecharge =
                accountRechargeRemoteService.getSumAccRechargeByGoalClient(accountRechargeVoDTO);
                
               /* SaxoAccountOrderReqDTO saxoAccountOrderReqQueryForDeposit = new SaxoAccountOrderReqDTO();
                saxoAccountOrderReqQueryForDeposit.setClientId(userGoalInfoResDTO.getClientId());
                saxoAccountOrderReqQueryForDeposit.setGoalId(userGoalInfoResDTO.getGoalId());
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
                clientGoalResVo.setTotalDeposit(rpcMessageAccountRecharge.getContent());
               }
                
               AccountRedeemVoDTO accountRedeemVoDTO = new AccountRedeemVoDTO();
               accountRedeemVoDTO.setClientId(userGoalInfoResDTO.getClientId());
               accountRedeemVoDTO.setGoalId(userGoalInfoResDTO.getGoalId());

                RpcMessage<BigDecimal> rpcMessageAccountRedeem =
                accountRedeemRemoteService.getSumRedeemConfirmAmtByGoalClient(accountRedeemVoDTO);

                /*SaxoAccountOrderReqDTO saxoAccountOrderReqQueryForWithdrawal = new SaxoAccountOrderReqDTO();
                saxoAccountOrderReqQueryForWithdrawal.setClientId(userGoalInfoResDTO.getClientId());
                saxoAccountOrderReqQueryForWithdrawal.setGoalId(userGoalInfoResDTO.getGoalId());
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
                clientGoalResVo.setTotalWithdrawal(rpcMessageAccountRedeem.getContent());
                }

                //查询goal的profitinfo
                BigDecimal fxImpact = BigDecimal.ZERO;
                BigDecimal totalReturn = BigDecimal.ZERO;
                BigDecimal portfolioReturn = BigDecimal.ZERO;
                UserProfitInfoReqDTO userProfitInfoReqDTO = new UserProfitInfoReqDTO();
                userProfitInfoReqDTO.setClientId(userGoalInfoResDTO.getClientId());
                userProfitInfoReqDTO.setGoalId(userGoalInfoResDTO.getGoalId());
                userProfitInfoReqDTO.setProfitDate(CalDateSupport.getCalDate());
                RpcMessage<List<UserProfitInfoResDTO>> rpcProfitInfo =
                        userStaticsRemoteService.getUserProfitInfos(userProfitInfoReqDTO);
                if(rpcProfitInfo.isSuccess()){
                    List<UserProfitInfoResDTO> userProfitInfoResDTOList = rpcProfitInfo.getContent();
                    for(UserProfitInfoResDTO userProfitInfoResDTO:userProfitInfoResDTOList){
                        fxImpact = fxImpact.add(userProfitInfoResDTO.getFxImpact());
                        portfolioReturn = portfolioReturn.add(userProfitInfoResDTO.getPortfolioProfit());
                        totalReturn = totalReturn.add(userProfitInfoResDTO.getTotalProfit());
                    }

                }
                clientGoalResVo.setTotalReturn(totalReturn);
                clientGoalResVo.setFxImpact(fxImpact);
                clientGoalResVo.setPortfolioReturn(portfolioReturn);


                CalDecimal<ClientGoalResBeanVo> clientGoalResBeanVoCalDecimal = new CalDecimal<>();
                clientGoalResBeanVoCalDecimal.handleDot(clientGoalResVo);


                clientGoalResVoList.add(clientGoalResVo);
            }

        }

        //查询bankvirtualaccount
//        BigDecimal totalSquirrelCashSgd = BigDecimal.ZERO;
//        BigDecimal totalSquirrelCashUsd = BigDecimal.ZERO;
//        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
//        bankVirtualAccountDTO.setClientId(clientGoalReqVo.getClientId());
//        List<BankVirtualAccountResDTO> bankVirtualAccountList =
//                userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);
//        for(BankVirtualAccountResDTO bankVirtualAccountRes:bankVirtualAccountList){
//            if(bankVirtualAccountRes.getCurrency() == CurrencyEnum.SGD){
//                totalSquirrelCashSgd = totalSquirrelCashSgd.add(bankVirtualAccountRes.getCashAmount());
//            }else{
//                totalSquirrelCashUsd = totalSquirrelCashUsd.add(bankVirtualAccountRes.getCashAmount());
//            }
//        }



//        Page<ClientGoalResVo> pagination = new Page<>();
//        ClientGoalResVo clientGoalResVo = new ClientGoalResVo();
//        clientGoalResVo.setClientId("1123");
//        clientGoalResVo.setCreateTime(DateUtils.now());
//        clientGoalResVo.setCurrency(CurrencyEnum.SGD);
//        clientGoalResVo.setExchangeRate(new BigDecimal("123.09"));
//        clientGoalResVo.setFxImpact(new BigDecimal("1.1112"));
//        clientGoalResVo.setGoalId("1123");
//        clientGoalResVo.setPortfolioId("PEWRESA");
//        clientGoalResVo.setAssetValue(new BigDecimal("1.9093"));
//        clientGoalResVo.setPortfolioReturn(new BigDecimal("1.298"));
//        clientGoalResVo.setReferenceCode("1.234");
//        clientGoalResVo.setTotalDeposit(new BigDecimal("11.23"));
//        clientGoalResVo.setTotalReturn(new BigDecimal("12.34"));

        //以下两个字段从前一个页面传过来
//        clientGoalResVo.setTotalWealthSgd(new BigDecimal("1.2093"));
//        clientGoalResVo.setTotalWealthUsd(new BigDecimal("1.2234"));
//        clientGoalResVo.setTotalWithdrawal(new BigDecimal("123.098"));

//        List<ClientGoalResVo> clientGoalResVoList = Lists.newArrayList();
//        clientGoalResVoList.add(clientGoalResVo);
//        pagination.setRecords(clientGoalResVoList);
        clientGoalRes.setClientGoalResBeanVoList(clientGoalResVoList);
//        BigDecimal totalWealthUsd = totalSquirrelCashUsd.add(totalAssetUsd);
        BigDecimal totalWealthSgd = totalAssetSgd;
//        clientGoalRes.setTotalWealthUsd(totalWealthUsd);
        clientGoalRes.setTotalWealthSgd(totalWealthSgd.setScale(2,BigDecimal.ROUND_DOWN));

        return Message.success(clientGoalRes);
    }

    @ApiOperation(value = "获取某个goal的etf的list")
    @PostMapping("/userAsset/goalEtfList")
    @RequiresPermissions("in:userAsset:read")
    public Message<List<ClientGoalEtfResVo>> goalEtfList(@RequestBody ClientGoalEtfReqVo clientGoalEtfReqVo) {

        //获取productinfo
        List<ProductInfoResDTO> productInfoResDTOList = modelServiceRemoteService.queryAllProductInfo();
        Map<String,ProductInfoResDTO> mapProduct = productInfoResDTOList
                .stream().collect(Collectors.toMap(ProductInfoResDTO::getProductCode , item -> item));


        UserEtfSharesReqDTO userEtfSharesReqDTO = new UserEtfSharesReqDTO();
        userEtfSharesReqDTO.setClientId(clientGoalEtfReqVo.getClientId());
        userEtfSharesReqDTO.setGoalId(clientGoalEtfReqVo.getGoalId());
        userEtfSharesReqDTO.setStaticDate(CalDateSupport.getCalDate());
        RpcMessage<List<UserEtfSharesResDTO>> rpcMessage_userEtf = userStaticsRemoteService.getUserEtfShares(userEtfSharesReqDTO);

        List<ClientGoalEtfResVo> clientGoalEtfResVoList = Lists.newArrayList();
        if(rpcMessage_userEtf.isSuccess()){
            List<UserEtfSharesResDTO> userEtfSharesResDTOList = rpcMessage_userEtf.getContent();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for(UserEtfSharesResDTO clientGoalEtfRes:userEtfSharesResDTOList){
                totalAmount = totalAmount.add(clientGoalEtfRes.getMoney());
            }
            BigDecimal totalPrecent = BigDecimal.ZERO;
            for(int i=0;i<userEtfSharesResDTOList.size();i++){
                UserEtfSharesResDTO userEtfSharesResDTO = userEtfSharesResDTOList.get(i);
                ClientGoalEtfResVo clientGoalEtfResVo = new ClientGoalEtfResVo();
//                clientGoalEtfResVo.setAmount(new BigDecimal("1.23"));
                if(userEtfSharesResDTO.getProductCode().equalsIgnoreCase("cash")){
                    clientGoalEtfResVo.setClassifyName("Cash");
                    clientGoalEtfResVo.setClientId(userEtfSharesResDTO.getClientId());
                    clientGoalEtfResVo.setGoalId(userEtfSharesResDTO.getGoalId());
                    clientGoalEtfResVo.setProductCode(userEtfSharesResDTO.getProductCode());
                    clientGoalEtfResVo.setShare(userEtfSharesResDTO.getShares());
                    clientGoalEtfResVo.setAmount(userEtfSharesResDTO.getMoney());
                    clientGoalEtfResVoList.add(clientGoalEtfResVo);
                    //continue;
                }else{
                    ProductInfoResDTO productInfoResDTO = mapProduct.get(userEtfSharesResDTO.getProductCode().toUpperCase());
                    clientGoalEtfResVo.setClassifyName(productInfoResDTO.getFirstClassfiyDesc());
                    clientGoalEtfResVo.setClientId(userEtfSharesResDTO.getClientId());
                    clientGoalEtfResVo.setGoalId(userEtfSharesResDTO.getGoalId());
                    clientGoalEtfResVo.setProductCode(userEtfSharesResDTO.getProductCode());
                    clientGoalEtfResVo.setShare(userEtfSharesResDTO.getShares());
                    clientGoalEtfResVo.setAmount(userEtfSharesResDTO.getMoney());
                }

                //如果是最后一个
                if(i == userEtfSharesResDTOList.size()-1){
                    BigDecimal precent = new BigDecimal("1").subtract(totalPrecent);
                    clientGoalEtfResVo.setPercentage(precent.multiply(new BigDecimal(100)));
                }else {
                    BigDecimal precent = userEtfSharesResDTO.getMoney().divide(totalAmount,4, BigDecimal.ROUND_HALF_UP);
                    totalPrecent = totalPrecent.add(precent);
                    clientGoalEtfResVo.setPercentage(precent.multiply(new BigDecimal(100)));
                }
                CalDecimal<ClientGoalEtfResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(clientGoalEtfResVo);
                clientGoalEtfResVoList.add(clientGoalEtfResVo);
            }

        }

//        ClientGoalEtfResVo clientGoalEtfResVo = new ClientGoalEtfResVo();
//        clientGoalEtfResVo.setAmount(new BigDecimal("1.23"));
//        clientGoalEtfResVo.setClassifyName("fix in");
//        clientGoalEtfResVo.setClientId("tttt");
//        clientGoalEtfResVo.setGoalId("N-12");
//        clientGoalEtfResVo.setProductCode("109As");
//        clientGoalEtfResVo.setShare(new BigDecimal("1.23"));
//        clientGoalEtfResVo.setPercentage(new BigDecimal("1"));
//
//        List<ClientGoalEtfResVo> clientGoalEtfResVoList = Lists.newArrayList();
//        clientGoalEtfResVoList.add(clientGoalEtfResVo);

        return Message.success(clientGoalEtfResVoList);
    }

    @ApiOperation(value = "获取某个client的transction")
    @PostMapping("/userAsset/goalTranList")
    @RequiresPermissions("in:userAsset:read")
    public Message<Page<ClientTransResVo>> goalTranList(@RequestBody ClientTransReqVo clientTransReqVo) {
        Page<ClientTransResVo> pagination = new Page<>();
        List<ClientTransResVo> clientTransResVoList = Lists.newArrayList();

        TransOrderReqDTO transOrderReqDTO = new TransOrderReqDTO();
        transOrderReqDTO.setPageNo(clientTransReqVo.getPageNo());
        transOrderReqDTO.setPageSize(clientTransReqVo.getPageSize());
        transOrderReqDTO.setClientId(clientTransReqVo.getClientId());
        transOrderReqDTO.setStartTranscationTime(clientTransReqVo.getTradeStartDate());
        transOrderReqDTO.setEndTranscationTime(clientTransReqVo.getTradeEndDate());
        RpcMessage<Page<TransOrderResDTO>> rpcMessagePageOrder
                = transRemoteService.getTransOrders(transOrderReqDTO);

        if(rpcMessagePageOrder.isSuccess()){
            //amountSgd获取
            Page<TransOrderResDTO> transOrderResDTOPagination = rpcMessagePageOrder.getContent();
            pagination = BeanMapperUtils.map(transOrderResDTOPagination,pagination.getClass());


            List<TransOrderResDTO> transOrderResDTOList = transOrderResDTOPagination.getRecords();

            for(TransOrderResDTO transOrderResDTO:transOrderResDTOList){
                ClientTransResVo clientTransResVo = new ClientTransResVo();
                clientTransResVo.setTransType(transOrderResDTO.getTransType());
                clientTransResVo.setTransTime(transOrderResDTO.getTranscationTime());
                clientTransResVo.setTransNo(transOrderResDTO.getTransNo());
                clientTransResVo.setGoalId(transOrderResDTO.getGoalId());
                clientTransResVo.setClientId(transOrderResDTO.getClientId());
                clientTransResVo.setAmountUsd(transOrderResDTO.getAmountUsd());
                clientTransResVo.setTransStatus(transOrderResDTO.getTransStatus());

                UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
                userGoalInfoDTO.setClientId(transOrderResDTO.getClientId());
                userGoalInfoDTO.setGoalId(transOrderResDTO.getGoalId());
                RpcMessage<UserGoalInfoResDTO> userGoalInfoResDTORpcMessage
                        = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
                if(userGoalInfoResDTORpcMessage.isSuccess()){
                    UserGoalInfoResDTO userGoalInfoResDTO = userGoalInfoResDTORpcMessage.getContent();
                    clientTransResVo.setReferenceCode(userGoalInfoResDTO.getReferenceCode());
                    clientTransResVo.setPortfolioId(userGoalInfoResDTO.getPortfolioId());
                }



                TransTypeEnum transType = transOrderResDTO.getTransType();
   /*             if(transType == TransTypeEnum.INVESTMENT){
                    //根据id找到对应交易记录
//                    SaxoAccountOrderReqDTO saxoAccountOrderQuery = new SaxoAccountOrderReqDTO();
//                    saxoAccountOrderQuery.setId(transOrderResDTO.getTransNo());
//                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderRpc
//                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderQuery);
//
//                    if(!saxoAccountOrderRpc.isSuccess()){
//                        continue;
//                    }
//                    SaxoAccountOrderResDTO resDTO = saxoAccountOrderRpc.getContent();
                    SaxoAccountOrderReqDTO saxoAccountOrderOutQuery = new SaxoAccountOrderReqDTO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
                    saxoAccountOrderOutQuery.setBankOrderNo(transOrderResDTO.getTransNo());
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                    saxoAccountOrderOutQuery.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderResDTORpcMessage
                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderOutQuery);

                    if(saxoAccountOrderResDTORpcMessage.isSuccess()){
                        SaxoAccountOrderResDTO saxoAccountOrderResDTO = saxoAccountOrderResDTORpcMessage.getContent();
                        clientTransResVo.setAmountSgd(saxoAccountOrderResDTO.getCashAmount());
                    }

                }else if(transType == TransTypeEnum.WITHDRAWAL){
                    //根据redeemapplyid,找到对应的出，计算费率
//                    SaxoAccountOrderReqDTO saxoAccountOrderQuery = new SaxoAccountOrderReqDTO();
//                    saxoAccountOrderQuery.setId(transOrderResDTO.getTransNo());
//                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderRpc
//                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderQuery);
//
//                    if(!saxoAccountOrderRpc.isSuccess()){
//                        continue;
//                    }

//                    SaxoAccountOrderResDTO resDTO = saxoAccountOrderRpc.getContent();
                    SaxoAccountOrderReqDTO saxoAccountOrderOutQuery = new SaxoAccountOrderReqDTO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
                    saxoAccountOrderOutQuery.setRedeemApplyId(Long.valueOf(transOrderResDTO.getTransNo()));
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                    saxoAccountOrderOutQuery.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);

                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderResDTORpcMessage
                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderOutQuery);

                    if(saxoAccountOrderResDTORpcMessage.isSuccess()){
                        SaxoAccountOrderResDTO saxoAccountOrderResDTO = saxoAccountOrderResDTORpcMessage.getContent();
                        clientTransResVo.setAmountSgd(saxoAccountOrderResDTO.getCashAmount());
                    }

                }else if(transType == TransTypeEnum.DIVIDEND ||
                        transType == TransTypeEnum.MGT ||
                        transType == TransTypeEnum.MGT_GST ||
                        transType == TransTypeEnum.CUSTODY){
                    //查询userstatic
                    UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
                    userStaticsReqDTO.setClientId(transOrderResDTO.getClientId());
                    userStaticsReqDTO.setGoalId(transOrderResDTO.getGoalId());
                    userStaticsReqDTO.setStaticDate(transOrderResDTO.getTranscationTime());
                    RpcMessage<UserStaticsResDTO> resDTORpcMessage
                            = userStaticsRemoteService.getUserStatic(userStaticsReqDTO);
                    if(resDTORpcMessage.isSuccess()){
                        UserStaticsResDTO userStaticsResDTO = resDTORpcMessage.getContent();
                        BigDecimal fxr = userStaticsResDTO.getFxRateForFundOut();
                        BigDecimal amountSgd = transOrderResDTO.getAmountUsd().multiply(fxr);
                        clientTransResVo.setAmountSgd(amountSgd);
                    }
                }
*/
                CalDecimal<ClientTransResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(clientTransResVo);
                clientTransResVoList.add(clientTransResVo);
            }

        }
        pagination.setRecords(clientTransResVoList);




//        Page<ClientTransResVo> pagination = new Page<>();
//        ClientTransResVo clientTransResVo = new ClientTransResVo();
//        clientTransResVo.setClientId("test");
//        clientTransResVo.setGoalId("G-91");
//        clientTransResVo.setPortfolioId("PA12");
//        clientTransResVo.setReferenceCode("AS34E");
//        clientTransResVo.setTransNo(102928383030L);
//        clientTransResVo.setTransTime(DateUtils.now());
//        clientTransResVo.setTransType(TransTypeEnum.DIVIDEND);

//        List<ClientTransResVo> clientTransResVoList = Lists.newArrayList();
//        clientTransResVoList.add(clientTransResVo);
//        pagination.setRecords(clientTransResVoList);
        return Message.success(pagination);
    }


    @ApiOperation(value = "导出某个client的transction")
    @GetMapping("/userAsset/exportGoalTranList")
    @RequiresPermissions("in:userAsset:read")
    public Message<List<ClientTransExportResVo>> exportGoalTranList(
            ClientTransExportReqVo clientTransReqVo,
            HttpServletResponse response) {
        List<ClientTransExportResVo> clientTransResVoList = Lists.newArrayList();

        TransOrderReqDTO transOrderReqDTO = new TransOrderReqDTO();
//        transOrderReqDTO.setClientId(clientTransReqVo.getClientId());
        transOrderReqDTO.setClientId(clientTransReqVo.getClientId());
        transOrderReqDTO.setStartTranscationTime(clientTransReqVo.getTradeStartDate());
        transOrderReqDTO.setEndTranscationTime(clientTransReqVo.getTradeEndDate());
        RpcMessage<List<TransOrderResDTO>> rpcMessagePageOrder
                = transRemoteService.getTransOrdersList(transOrderReqDTO);

        if(rpcMessagePageOrder.isSuccess()){
            //amountSgd获取
//            Page<TransOrderResDTO> transOrderResDTOPagination = rpcMessagePageOrder.getContent();
//            pagination = BeanMapperUtils.map(transOrderResDTOPagination,pagination.getClass());

            List<TransOrderResDTO> transOrderResDTOList = rpcMessagePageOrder.getContent();

            for(TransOrderResDTO transOrderResDTO:transOrderResDTOList){
                ClientTransExportResVo clientTransResVo = new ClientTransExportResVo();
                clientTransResVo.setTransType(transOrderResDTO.getTransType());
                clientTransResVo.setTransTime(transOrderResDTO.getTranscationTime());
                clientTransResVo.setTransNo(transOrderResDTO.getTransNo());
                clientTransResVo.setGoalId(transOrderResDTO.getGoalId());
                clientTransResVo.setClientId(transOrderResDTO.getClientId());
                clientTransResVo.setAmountUsd(transOrderResDTO.getAmountUsd());
                clientTransResVo.setTransStatus(transOrderResDTO.getTransStatus());

                UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
                userGoalInfoDTO.setClientId(transOrderResDTO.getClientId());
                userGoalInfoDTO.setGoalId(transOrderResDTO.getGoalId());
                RpcMessage<UserGoalInfoResDTO> userGoalInfoResDTORpcMessage
                        = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
                if(userGoalInfoResDTORpcMessage.isSuccess()){
                    UserGoalInfoResDTO userGoalInfoResDTO = userGoalInfoResDTORpcMessage.getContent();
                    clientTransResVo.setReferenceCode(userGoalInfoResDTO.getReferenceCode());
                    clientTransResVo.setPortfolioId(userGoalInfoResDTO.getPortfolioId());
                }



                TransTypeEnum transType = transOrderResDTO.getTransType();
                if(transType == TransTypeEnum.INVESTMENT){
                    //根据id找到对应交易记录
                    SaxoAccountOrderReqDTO saxoAccountOrderOutQuery = new SaxoAccountOrderReqDTO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.UOBTOSAXO);
                    saxoAccountOrderOutQuery.setBankOrderNo(transOrderResDTO.getTransNo());
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                    saxoAccountOrderOutQuery.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);
                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderResDTORpcMessage
                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderOutQuery);

                    if(saxoAccountOrderResDTORpcMessage.isSuccess()){
                        SaxoAccountOrderResDTO saxoAccountOrderResDTO = saxoAccountOrderResDTORpcMessage.getContent();
                        clientTransResVo.setAmountSgd(saxoAccountOrderResDTO.getCashAmount());
                    }

                }else if(transType == TransTypeEnum.WITHDRAWAL){
                    //根据redeemapplyid,找到对应的出，计算费率
                    SaxoAccountOrderReqDTO saxoAccountOrderOutQuery = new SaxoAccountOrderReqDTO();
                    saxoAccountOrderOutQuery.setActionType(SaxoOrderActionTypeEnum.REDEEM_EXCHANGE);
                    saxoAccountOrderOutQuery.setRedeemApplyId(Long.valueOf(transOrderResDTO.getTransNo()));
                    saxoAccountOrderOutQuery.setOperatorType(SaxoOrderTradeTypeEnum.COME_INTO);
                    saxoAccountOrderOutQuery.setOrderStatus(SaxoOrderTradeStatusEnum.SUCCESS);

                    RpcMessage<SaxoAccountOrderResDTO> saxoAccountOrderResDTORpcMessage
                            = saxoAccountOrderRemoteService.getSaxoAccountOrder(saxoAccountOrderOutQuery);

                    if(saxoAccountOrderResDTORpcMessage.isSuccess()){
                        SaxoAccountOrderResDTO saxoAccountOrderResDTO = saxoAccountOrderResDTORpcMessage.getContent();
                        clientTransResVo.setAmountSgd(saxoAccountOrderResDTO.getCashAmount());
                    }

                }else if(transType == TransTypeEnum.DIVIDEND ||
                        transType == TransTypeEnum.MGT ||
                        transType == TransTypeEnum.MGT_GST ||
                        transType == TransTypeEnum.CUSTODY){
                    //查询userstatic
                    UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
                    userStaticsReqDTO.setClientId(transOrderResDTO.getClientId());
                    userStaticsReqDTO.setGoalId(transOrderResDTO.getGoalId());
                    userStaticsReqDTO.setStaticDate(transOrderResDTO.getTranscationTime());
                    RpcMessage<UserStaticsResDTO> resDTORpcMessage
                            = userStaticsRemoteService.getUserStatic(userStaticsReqDTO);
                    if(resDTORpcMessage.isSuccess()){
                        UserStaticsResDTO userStaticsResDTO = resDTORpcMessage.getContent();
                        BigDecimal fxr = userStaticsResDTO.getFxRateForFundOut();
                        BigDecimal amountSgd = transOrderResDTO.getAmountUsd().multiply(fxr);
                        clientTransResVo.setAmountSgd(amountSgd);
                    }
                }

                CalDecimal<ClientTransExportResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(clientTransResVo);
                clientTransResVoList.add(clientTransResVo);
            }

        }

        if(clientTransResVoList.size()==0){
            Message.error("无内容");
        }
        ExportExcel exportExcel = new ExportExcel(null, ClientTransExportResVo.class);
        exportExcel.setDataList(clientTransResVoList);

        String dateStr = DateUtils.formatDate(new Date(),"yyyyMMdd");
        String fileName = "Client_Transction_"+dateStr+".xlsx";
        try
        {
            exportExcel.write(response,fileName);
            exportExcel.dispose();
        }
        catch (Exception e)
        {
            return Message.error("下载文件失败"+e.getMessage());
        }

        return null;
    }





    @ApiOperation(value = "获取某个client的银行流水")
    @PostMapping("/userAsset/bankOrderList")
    @RequiresPermissions("in:userAsset:read")
    public Message<BankOrderListResVo> bankOrderList(@RequestBody BankOrderReqVo bankOrderReqVo) {
        BankOrderListResVo bankOrderListResVo = new BankOrderListResVo();

        Page<BankOrderResVo> paginationRes = new Page<>();
        List<BankOrderResVo> bankOrderResVoList = Lists.newArrayList();

        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
        bankVirtualAccountDTO.setClientId(bankOrderReqVo.getClientId());
        List<BankVirtualAccountResDTO> bankVirtualAccountResDTOList
                = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);


        List<String> virtualAccountNos = Lists.newArrayList();
        for(BankVirtualAccountResDTO bankVirtualAccountResDTO:bankVirtualAccountResDTOList){
            if(bankVirtualAccountResDTO.getCurrency() == CurrencyEnum.SGD){
                bankOrderListResVo.setTotalAvaiableSgd(bankVirtualAccountResDTO.getCashAmount());
//                bankOrderListResVo.setTotalAvaiableUsd();
            }else{
//                bankOrderListResVo.setTotalAvaiableSgd();
                bankOrderListResVo.setTotalAvaiableUsd(bankVirtualAccountResDTO.getCashAmount());
            }


            virtualAccountNos.add(bankVirtualAccountResDTO.getVirtualAccountNo());
        }
        BankVirtualAccountOrderDTO dto = new BankVirtualAccountOrderDTO();
        dto.setVirtualAccountNoList(virtualAccountNos);
        dto.setPageNo(bankOrderReqVo.getPageNo());
        dto.setPageSize(bankOrderReqVo.getPageSize());
        dto.setStartTradeTime(bankOrderReqVo.getTradeStartDate());
        dto.setEndTradeTime(bankOrderReqVo.getTradeEndDate());
        RpcMessage<Page<BankVirtualAccountOrderResDTO>> rpcMessage =
                userServiceRemoteService.queryBankVirtualAccountOrderPage(dto);



        if(rpcMessage.isSuccess()){
            Page<BankVirtualAccountOrderResDTO> pagination = rpcMessage.getContent();
            paginationRes = BeanMapperUtils.map(pagination,paginationRes.getClass());
            List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOList = pagination.getRecords();
            for(BankVirtualAccountOrderResDTO bankVirtualAccountOrderResDTO:bankVirtualAccountOrderResDTOList){
                BankOrderResVo bankOrderResVo = new BankOrderResVo();
                bankOrderResVo.setVirtualAccountNo(bankVirtualAccountOrderResDTO.getVirtualAccountNo());
                if(bankVirtualAccountOrderResDTO.getOperatorType() == VAOrderTradeTypeEnum.COME_INTO){
                    bankOrderResVo.setAvaiableAmount("+"+bankVirtualAccountOrderResDTO.getCashAmount()
                            .setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }else{
                    bankOrderResVo.setAvaiableAmount("-"+bankVirtualAccountOrderResDTO.getCashAmount()
                            .setScale(2,BigDecimal.ROUND_HALF_UP).toString());
                }
//                bankOrderResVo.setAvaiableAmount(bankVirtualAccountOrderResDTO.getCashAmount());
                bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getBankOrderNo());
                bankOrderResVo.setCurrency(bankVirtualAccountOrderResDTO.getCurrency());
                bankOrderResVo.setTradeTime(bankVirtualAccountOrderResDTO.getTradeTime());

                if(bankVirtualAccountOrderResDTO.getMatchType() == MatchTypeEnum.NAME_UNMATCH){
                    bankOrderResVo.setMatchStatusDesc("Name Unmatch");

                }else if(bankVirtualAccountOrderResDTO.getMatchType() == MatchTypeEnum.REFERENCECODE_UNMATCH){
                    bankOrderResVo.setMatchStatusDesc("Reference Code Unmatch");

                }else{
                    bankOrderResVo.setMatchStatusDesc("Match");

                }

                bankOrderResVo.setReferenceCode(bankVirtualAccountOrderResDTO.getReferenceCode());
                if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.RECHARGE){
                    if(StringUtils.isEmpty(bankVirtualAccountOrderResDTO.getReferenceCode())){
                        bankOrderResVo.setTypeDesc("Deposit to squirrel cash");
                    }else{
                        bankOrderResVo.setTypeDesc("Deposit for goal");
                    }
                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REDEEM_EXCHANGE){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("exchange SGD to USD");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.RECHARGE_EXCHANGE){
                    bankOrderResVo.setTypeDesc("exchange USD to SGD");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REFUND){
                    bankOrderResVo.setTypeDesc("Refund");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.SAXOTOUOB){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("SAXO to UOB transfer");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REDEEM){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("withdrawal");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.UOBTOSAXO){
                    bankOrderResVo.setTypeDesc("Transfer to SAXO");

                }
                CalDecimal<BankOrderResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(bankOrderResVo);
                bankOrderResVoList.add(bankOrderResVo);
            }
        }

//        BankOrderResVo bankOrderResVo = new BankOrderResVo();
//        bankOrderResVo.setAvaiableAmount(new BigDecimal("2.13"));
//        bankOrderResVo.setBankOrderNo("12233e");
//        bankOrderResVo.setCurrency(CurrencyEnum.SGD);
//        bankOrderResVo.setMatchStatusDesc("unmatch");
//        bankOrderResVo.setReferenceCode("ASEesj2");
//        bankOrderResVo.setTypeDesc("Un");

//        bankOrderResVoList.add(bankOrderResVo);
        CalDecimal<BankOrderListResVo> calDecimal = new CalDecimal<>();
        calDecimal.handleDot(bankOrderListResVo);


        paginationRes.setRecords(bankOrderResVoList);
        bankOrderListResVo.setPage(paginationRes);

        return Message.success(bankOrderListResVo);
    }



    @ApiOperation(value = "导出某个client的银行流水")
    @GetMapping("/userAsset/bankOrderExport")
    @RequiresPermissions("in:userAsset:read")
    public Message bankOrderExport(BankOrderExportReqVo bankOrderExportReqVo,
                                   HttpServletResponse response) {
        List<BankOrderExportResVo> bankOrderResVoList = Lists.newArrayList();

        BankVirtualAccountDTO bankVirtualAccountDTO = new BankVirtualAccountDTO();
        bankVirtualAccountDTO.setClientId(bankOrderExportReqVo.getClientId());
        List<BankVirtualAccountResDTO> bankVirtualAccountResDTOList
                = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccountDTO);


        List<String> virtualAccountNos = Lists.newArrayList();
        for(BankVirtualAccountResDTO bankVirtualAccountResDTO:bankVirtualAccountResDTOList){
            virtualAccountNos.add(bankVirtualAccountResDTO.getVirtualAccountNo());
        }
        BankVirtualAccountOrderDTO dto = new BankVirtualAccountOrderDTO();
        dto.setVirtualAccountNoList(virtualAccountNos);
        dto.setStartTradeTime(bankOrderExportReqVo.getTradeStartDate());
        dto.setEndTradeTime(bankOrderExportReqVo.getTradeEndDate());
        RpcMessage<List<BankVirtualAccountOrderResDTO>> rpcMessage =
                userServiceRemoteService.listBankVirtualAccountOrders(dto);


        if(rpcMessage.isSuccess()){
            List<BankVirtualAccountOrderResDTO> bankVirtualAccountOrderResDTOList = rpcMessage.getContent();
            for(BankVirtualAccountOrderResDTO bankVirtualAccountOrderResDTO:bankVirtualAccountOrderResDTOList){
                BankOrderExportResVo bankOrderResVo = new BankOrderExportResVo();
                bankOrderResVo.setVirtualAccountNo(bankVirtualAccountOrderResDTO.getVirtualAccountNo());
                if(bankVirtualAccountOrderResDTO.getOperatorType() == VAOrderTradeTypeEnum.COME_INTO){
                    bankOrderResVo.setAvaiableAmount("+"+bankVirtualAccountOrderResDTO.getCashAmount().toString());
                }else{
                    bankOrderResVo.setAvaiableAmount("-"+bankVirtualAccountOrderResDTO.getCashAmount().toString());
                }

                bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getBankOrderNo());
                bankOrderResVo.setCurrency(bankVirtualAccountOrderResDTO.getCurrency());
                bankOrderResVo.setTradeTime(bankVirtualAccountOrderResDTO.getTradeTime());

                if(bankVirtualAccountOrderResDTO.getMatchType() == MatchTypeEnum.NAME_UNMATCH){
                    bankOrderResVo.setMatchStatusDesc("Name Unmatch");

                }else if(bankVirtualAccountOrderResDTO.getMatchType() == MatchTypeEnum.REFERENCECODE_UNMATCH){
                    bankOrderResVo.setMatchStatusDesc("Reference Code Unmatch");

                }else{
                    bankOrderResVo.setMatchStatusDesc("Match");

                }




                bankOrderResVo.setReferenceCode(bankVirtualAccountOrderResDTO.getReferenceCode());
                if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.RECHARGE){
                    if(StringUtils.isEmpty(bankVirtualAccountOrderResDTO.getReferenceCode())){
                        bankOrderResVo.setTypeDesc("Deposit to squirrel cash");
                    }else{
                        bankOrderResVo.setTypeDesc("Deposit for goal");
                    }
                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REDEEM_EXCHANGE){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("exchange SGD to USD");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.RECHARGE_EXCHANGE){
                    bankOrderResVo.setTypeDesc("exchange USD to SGD");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REFUND){
                    bankOrderResVo.setTypeDesc("Refund");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.SAXOTOUOB){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("SAXO to UOB transfer");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.REDEEM){
                    bankOrderResVo.setBankOrderNo(bankVirtualAccountOrderResDTO.getRedeemApplyId().toString());
                    bankOrderResVo.setTypeDesc("withdrawal");

                }else if(bankVirtualAccountOrderResDTO.getActionType() == VAOrderActionTypeEnum.UOBTOSAXO){
                    bankOrderResVo.setTypeDesc("Transfer to SAXO");

                }
                CalDecimal<BankOrderExportResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(bankOrderResVo);
                bankOrderResVoList.add(bankOrderResVo);
            }
        }
        if(bankOrderResVoList.size()==0){
            Message.error("无内容");
        }
        ExportExcel exportExcel = new ExportExcel(null, BankOrderExportResVo.class);
        exportExcel.setDataList(bankOrderResVoList);

        String dateStr = DateUtils.formatDate(new Date(),"yyyyMMdd");
        String fileName = "Client_BankLedger_"+dateStr+".xlsx";
        try
        {
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//            response.setHeader("Content-Disposition", "attachment;fileName=" +  fileName);
//            response.reset();
            exportExcel.write(response,fileName);
            exportExcel.dispose();
        }
        catch (Exception e)
        {
            return Message.error("下载文件失败"+e.getMessage());
        }
        return null;
    }


}
