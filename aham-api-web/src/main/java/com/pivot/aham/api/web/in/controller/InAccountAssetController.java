package com.pivot.aham.api.web.in.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.AccountAssetReqDTO;
import com.pivot.aham.api.server.dto.AccountAssetResDTO;
import com.pivot.aham.api.server.dto.req.*;
import com.pivot.aham.api.server.dto.res.*;
import com.pivot.aham.api.server.remoteservice.*;
import com.pivot.aham.api.web.in.vo.*;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.in.DividendTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.HashMap;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/v1/in")
@Api(value = "账户资产管理", description = "账户资产管理接口")
public class InAccountAssetController {
    @Resource
    private AccountInfoRemoteService accountInfoRemoteService;
    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;
    @Resource
    private AccountStaticsRemoteService accountStaticsRemoteService;
    @Resource
    private TransRemoteService transRemoteService;
    @Resource
    private AccountReBalanceRemoteService accountReBalanceRemoteService;
    @Resource
    private AccountUserRemoteService accountUserRemoteService;

    @ApiOperation(value = "获取account列表(分页)")
    @PostMapping("/accountAsset/accountList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<Page<AccountInfoResVo>> accountList(@RequestBody AccountInfoReqVo accountInfoReqVo) {
        Page<AccountInfoResVo> pagination = new Page<>();
        List<AccountInfoResVo> accountInfoResVoList = Lists.newArrayList();

        AccountInfoReqDTO accountInfoReqDTO = new AccountInfoReqDTO();
        accountInfoReqDTO.setPageNo(accountInfoReqVo.getPageNo());
        accountInfoReqDTO.setPageSize(accountInfoReqVo.getPageSize());
//        accountInfoReqDTO.setId(accountInfoReqVo.getAccountId());
        accountInfoReqDTO.setLikeAccountId(accountInfoReqVo.getAccountId());
        RpcMessage<Page<AccountInfoResDTO>> accountInfoPageRpc
                =  accountInfoRemoteService.getAccountInfoPage(accountInfoReqDTO);

        if(accountInfoPageRpc.isSuccess()){
            Page<AccountInfoResDTO> accountInfoPage = accountInfoPageRpc.getContent();
            pagination = BeanMapperUtils.map(accountInfoPage,pagination.getClass());

            List<AccountInfoResDTO> accountInfoResList = accountInfoPage.getRecords();
            for(AccountInfoResDTO accountInfoResDTO:accountInfoResList){
                AccountInfoResVo accountInfoResVo = new AccountInfoResVo();
                accountInfoResVo.setAccountId(accountInfoResDTO.getId());
                accountInfoResVo.setAccountType(accountInfoResDTO.getInvestType());
                //获取资产etf总份额
//                BigDecimal totalShares = BigDecimal.ZERO;
//                AccountAssetReqDTO accountAssetDTO = new AccountAssetReqDTO();
//                accountAssetDTO.setAccountId(accountInfoResDTO.getId());
//                RpcMessage<List<AccountAssetResDTO>> accountAssetRpc
//                        = assetServiceRemoteService.queryAccountAssets(accountAssetDTO);
//                if(accountAssetRpc.isSuccess()){
//                    List<AccountAssetResDTO> accountAssetResDTOList = accountAssetRpc.getContent();
//                    for(AccountAssetResDTO accountAssetResDTO:accountAssetResDTOList){
//                        totalShares = totalShares.add(accountAssetResDTO.getProductShare());
//                    }
//                }



                HashMap<Long, String> hmap = new HashMap<Long, String>();
                AccountUserReqDTO accountUserReq = new AccountUserReqDTO();
                accountUserReq.setAccountId(accountInfoResDTO.getId());
                RpcMessage<List<AccountUserResDTO>> rpcMessage = accountUserRemoteService.getAccountUserList(accountUserReq);
                if(rpcMessage.isSuccess()) {
                    List<AccountUserResDTO> accountUserResDTOList = rpcMessage.getContent();
                    accountInfoResVo.setNumOfClients(accountUserResDTOList.size());
                    for(AccountUserResDTO accountUserResDTO : accountUserResDTOList){
                        hmap.put(accountUserResDTO.getAccountId(), accountUserResDTO.getGoalId());
                    }
                }

                //获取accountstatics
                AccountStaticsReqDTO accountStaticsReqDTO = new AccountStaticsReqDTO();
                accountStaticsReqDTO.setAccountId(accountInfoResDTO.getId());
                //Date yesterday = DateUtils.addDateByDay(DateUtils.now(), -2);
                accountStaticsReqDTO.setStaticDate(CalDateSupport.getCalYesDate());
                //accountStaticsReqDTO.setStaticDate(yesterday);
                RpcMessage<AccountStaticsResDTO> rpcMessageAccountStatics
                        = accountStaticsRemoteService.selectByStaticDate(accountStaticsReqDTO);
                if(rpcMessageAccountStatics.isSuccess()){
                    AccountStaticsResDTO accountStaticsResDTO = rpcMessageAccountStatics.getContent();
                    accountInfoResVo.setCustFee(accountStaticsResDTO.getCustFee());
                    accountInfoResVo.setDividend(accountStaticsResDTO.getCashDividend());
                    accountInfoResVo.setFundNav(accountStaticsResDTO.getNavInUsd());
                    accountInfoResVo.setMgtFee(accountStaticsResDTO.getMgtFee());
                    accountInfoResVo.setMgtGst(accountStaticsResDTO.getGstMgtFee());
                    accountInfoResVo.setTotalAsset(accountStaticsResDTO.getAdjFundAsset());
                    accountInfoResVo.setTotalCash(accountStaticsResDTO.getCashHolding());
                    accountInfoResVo.setTotalShares(accountStaticsResDTO.getAdjFundShares());
                    accountInfoResVo.setTotalAssetSGD(accountStaticsResDTO.getAdjFundAssetInSgd());
                    accountInfoResVo.setStaticDate(accountStaticsResDTO.getStaticDate());
                    String goalId = hmap.get(accountStaticsResDTO.getAccountId());
                    accountInfoResVo.setGoalId(goalId);
                }
                CalDecimal<AccountInfoResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountInfoResVo);

                accountInfoResVoList.add(accountInfoResVo);
            }
        }

//        Page<AccountInfoResVo> pagination = new Page<>();
//        AccountInfoResVo accountInfoResVo = new AccountInfoResVo();
//        accountInfoResVo.setAccountId("1123");
//        accountInfoResVo.setAccountType(AccountTypeEnum.TAILER);
//        accountInfoResVo.setCustFee(new BigDecimal("1.223"));
//        accountInfoResVo.setDividend(new BigDecimal("1.223"));
//        accountInfoResVo.setFundNav(new BigDecimal("1.223"));
//        accountInfoResVo.setMgtFee(new BigDecimal("1.223"));
//        accountInfoResVo.setMgtGst(new BigDecimal("1.223"));
//        accountInfoResVo.setTotalAsset(new BigDecimal("1.223"));
//        accountInfoResVo.setTotalCash(new BigDecimal("1.223"));
//        accountInfoResVo.setTotalShares(new BigDecimal("1.223"));

//        List<AccountInfoResVo> accountInfoResVoList = Lists.newArrayList();
//        accountInfoResVoList.add(accountInfoResVo);
        pagination.setRecords(accountInfoResVoList);
        return Message.success(pagination);
    }

    @ApiOperation(value = "获取account的Etf列表")
    @PostMapping("/accountAsset/accountEtfList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<List<AccountEtfResVo>> accountEtfList(@RequestBody AccountEtfReqVo accountEtfReqVo) {

        List<AccountEtfResVo> accountEtfResVoList = Lists.newArrayList();

        AccountAssetReqDTO accountAssetDTO = new AccountAssetReqDTO();
        accountAssetDTO.setAccountId(accountEtfReqVo.getAccountId());
        RpcMessage<List<AccountAssetResDTO>> accountAssetRpc
                = assetServiceRemoteService.queryAccountAssets(accountAssetDTO);
        if(accountAssetRpc.isSuccess()){
            List<AccountAssetResDTO> accountAssetResDTOList = accountAssetRpc.getContent();
            for(AccountAssetResDTO accountAssetRes:accountAssetResDTOList){
                AccountEtfResVo accountEtfResVo = new AccountEtfResVo();
                accountEtfResVo.setAmount(accountAssetRes.getProductMoney());
                accountEtfResVo.setProductCode(accountAssetRes.getProductCode());
                accountEtfResVo.setShare(accountAssetRes.getProductShare());


                CalDecimal<AccountEtfResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountEtfResVo);
                accountEtfResVoList.add(accountEtfResVo);
            }
        }

//        AccountEtfResVo accountEtfResVo = new AccountEtfResVo();
//        accountEtfResVo.setAmount(new BigDecimal("11.22"));
//        accountEtfResVo.setProductCode("12AASSS");
//        accountEtfResVo.setShare(new BigDecimal("1.234"));
//
//        List<AccountEtfResVo> accountEtfResVoList = Lists.newArrayList();
//        accountEtfResVoList.add(accountEtfResVo);

        return Message.success(accountEtfResVoList);
    }

    @ApiOperation(value = "获取account的saxo的order列表")
    @PostMapping("/accountAsset/saxoAccountOrderList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<Page<SaxoAccountOrderResVo>> saxoAccountOrderList(@RequestBody SaxoAccountOrderReqVo saxoAccountOrderReqVo) {
        Page<SaxoAccountOrderResVo> pagination = new Page<>();
        List<SaxoAccountOrderResVo> saxoAccountOrderResVoList = Lists.newArrayList();

        TmpOrderRecordReqDTO tmpOrderRecordReqDTO = new TmpOrderRecordReqDTO();
        tmpOrderRecordReqDTO.setAccountId(saxoAccountOrderReqVo.getAccountId());
        tmpOrderRecordReqDTO.setPageNo(saxoAccountOrderReqVo.getPageNo());
        tmpOrderRecordReqDTO.setPageSize(saxoAccountOrderReqVo.getPageSize());
        tmpOrderRecordReqDTO.setStartApplyTime(saxoAccountOrderReqVo.getStartApplyTime());
        tmpOrderRecordReqDTO.setEndApplyTime(saxoAccountOrderReqVo.getEndApplyTime());
        RpcMessage<Page<TmpOrderRecordResDTO>> rpcMessage = transRemoteService.getTmpOrders(tmpOrderRecordReqDTO);

        if(rpcMessage.isSuccess()){
            Page<TmpOrderRecordResDTO> tmpOrderPage = rpcMessage.getContent();
            pagination = BeanMapperUtils.map(tmpOrderPage,pagination.getClass());

            List<TmpOrderRecordResDTO> tmpOrderRecordResDTOS = tmpOrderPage.getRecords();

            for(TmpOrderRecordResDTO tmpOrderRecordResDTO:tmpOrderRecordResDTOS){
                SaxoAccountOrderResVo saxoAccountOrderResVo = new SaxoAccountOrderResVo();
                saxoAccountOrderResVo.setAccountId(tmpOrderRecordResDTO.getAccountId());
                saxoAccountOrderResVo.setAmount(tmpOrderRecordResDTO.getConfirmMoney());
                saxoAccountOrderResVo.setCommission(tmpOrderRecordResDTO.getTransCost());
                saxoAccountOrderResVo.setProductCode(tmpOrderRecordResDTO.getProductCode());
                saxoAccountOrderResVo.setSaxoOrderTransType(tmpOrderRecordResDTO.getTmpOrderTradeType());
                saxoAccountOrderResVo.setShares(tmpOrderRecordResDTO.getConfirmTradeShares());
                saxoAccountOrderResVo.setTransNo(tmpOrderRecordResDTO.getId().toString());
                saxoAccountOrderResVo.setTransTime(tmpOrderRecordResDTO.getApplyTime());

                CalDecimal<SaxoAccountOrderResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(saxoAccountOrderResVo);

                saxoAccountOrderResVoList.add(saxoAccountOrderResVo);
            }

        }

//        Page<SaxoAccountOrderResVo> pagination = new Page<>();
//        SaxoAccountOrderResVo saxoAccountOrderResVo = new SaxoAccountOrderResVo();
//        saxoAccountOrderResVo.setAccountId("11");
//        saxoAccountOrderResVo.setAmount(new BigDecimal("1.23344"));
//        saxoAccountOrderResVo.setCommission(new BigDecimal("1.23344"));
//        saxoAccountOrderResVo.setProductCode("SDF");
//        saxoAccountOrderResVo.setSaxoOrderTransType(SaxoOrderTransTypeEnum.BUY);
//        saxoAccountOrderResVo.setShares(new BigDecimal("1.23344"));
//        saxoAccountOrderResVo.setTransNo("1128373ddd");

//        List<SaxoAccountOrderResVo> saxoAccountOrderResVoList = Lists.newArrayList();
//        saxoAccountOrderResVoList.add(saxoAccountOrderResVo);
        pagination.setRecords(saxoAccountOrderResVoList);
        return Message.success(pagination);
    }

    @ApiOperation(value = "获取account的调仓记录列表")
    @PostMapping("/accountAsset/saxoAccountBalList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<Page<AccountBalRecordResVo>> saxoAccountBalList(@RequestBody AccountBalRecordReqVo accountBalRecordReqVo) {
        Page<AccountBalRecordResVo> paginationRes = new Page<>();
        List<AccountBalRecordResVo> accountBalRecordResVoList = Lists.newArrayList();

        BalanceRecordReqDTO balanceRecordReqDTO = new BalanceRecordReqDTO();
        balanceRecordReqDTO.setAccountId(accountBalRecordReqVo.getAccountId());
        balanceRecordReqDTO.setPageNo(accountBalRecordReqVo.getPageNo());
        balanceRecordReqDTO.setPageSize(accountBalRecordReqVo.getPageSize());
        balanceRecordReqDTO.setStartBalStartTime(accountBalRecordReqVo.getBalStartTime());
        balanceRecordReqDTO.setEndBalStartTime(accountBalRecordReqVo.getBalEndTime());
        RpcMessage<Page<BalanceRecordResDTO>> paginationRpcMessage =
                accountReBalanceRemoteService.getBalanceRecords(balanceRecordReqDTO);
        if(paginationRpcMessage.isSuccess()){
            Page<BalanceRecordResDTO> pagination = paginationRpcMessage.getContent();
            paginationRes = BeanMapperUtils.map(pagination,paginationRes.getClass());

            List<BalanceRecordResDTO> balanceRecordResDTOS = pagination.getRecords();
            for(BalanceRecordResDTO balanceRecordResDTO:balanceRecordResDTOS){
                AccountBalRecordResVo accountBalRecordResVo = new AccountBalRecordResVo();
                accountBalRecordResVo.setAccountId(balanceRecordResDTO.getAccountId().toString());
                accountBalRecordResVo.setBalId(balanceRecordResDTO.getId().toString());
                accountBalRecordResVo.setBalTime(balanceRecordResDTO.getBalStartTime());
                accountBalRecordResVo.setPortfolioId(balanceRecordResDTO.getPortfolioId());
                accountBalRecordResVo.setBalStatus(balanceRecordResDTO.getBalStatus());

                CalDecimal<AccountBalRecordResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountBalRecordResVo);
                accountBalRecordResVoList.add(accountBalRecordResVo);
            }
        }
//        Page<AccountBalRecordResVo> pagination = new Page<>();
//        AccountBalRecordResVo accountBalRecordResVo = new AccountBalRecordResVo();
//        accountBalRecordResVo.setAccountId("11");
//        accountBalRecordResVo.setBalId("111");
//        accountBalRecordResVo.setBalTime(DateUtils.now());
//        accountBalRecordResVo.setPortfolioId("SW345");

//        List<AccountBalRecordResVo> accountBalRecordResVoList = Lists.newArrayList();

        paginationRes.setRecords(accountBalRecordResVoList);
        return Message.success(paginationRes);
    }

    @ApiOperation(value = "获取account的调仓方案列表")
    @PostMapping("/accountAsset/saxoAccountAdjDetailList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<List<AccountBalAdjDetailResVo>> saxoAccountAdjDetailList(@RequestBody AccountBalAdjDetailReqVo accountBalAdjDetailReqVo) {
        List<AccountBalAdjDetailResVo> accountBalAdjDetailResVoList = Lists.newArrayList();


        AccountBalanceAdjDetailReqDTO accountBalanceAdjDetailResDTO = new AccountBalanceAdjDetailReqDTO();
        accountBalanceAdjDetailResDTO.setBalId(accountBalAdjDetailReqVo.getBalId());
        RpcMessage<List<AccountBalanceAdjDetailResDTO>> rpcMessageList
                = accountReBalanceRemoteService.getBalanceAdjDetails(accountBalanceAdjDetailResDTO);
        if(rpcMessageList.isSuccess()){
            List<AccountBalanceAdjDetailResDTO> accountBalanceAdjDetailResDTOS = rpcMessageList.getContent();
            for(AccountBalanceAdjDetailResDTO accountBalanceAdjDetailRes:accountBalanceAdjDetailResDTOS){
                AccountBalAdjDetailResVo accountBalAdjDetailResVo = new AccountBalAdjDetailResVo();
                accountBalAdjDetailResVo.setBalId(accountBalanceAdjDetailRes.getBalId().toString());
                accountBalAdjDetailResVo.setCurrentHold(accountBalanceAdjDetailRes.getCurrentHold());
                accountBalAdjDetailResVo.setProductCode(accountBalanceAdjDetailRes.getProductCode());
                accountBalAdjDetailResVo.setTargetHold(accountBalanceAdjDetailRes.getTargetHold());
                accountBalAdjDetailResVo.setTradeAmount(accountBalanceAdjDetailRes.getTradeAmount());
                accountBalAdjDetailResVo.setExecuteStatus(accountBalanceAdjDetailRes.getExecuteStatus());
                accountBalAdjDetailResVo.setTradeType(accountBalanceAdjDetailRes.getTradeType());


                CalDecimal<AccountBalAdjDetailResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountBalAdjDetailResVo);
                accountBalAdjDetailResVoList.add(accountBalAdjDetailResVo);
            }
        }
//        Page<AccountBalAdjDetailResVo> pagination = new Page<>();
//        AccountBalAdjDetailResVo accountBalAdjDetailResVo = new AccountBalAdjDetailResVo();
//        accountBalAdjDetailResVo.setBalId("1111");
//        accountBalAdjDetailResVo.setCurrentHold(new BigDecimal("1.29383"));
//        accountBalAdjDetailResVo.setProductCode("ASD123");
//        accountBalAdjDetailResVo.setTargetHold(new BigDecimal("12.234"));
//        accountBalAdjDetailResVo.setTradeAmount(new BigDecimal("1.23445"));

//        List<AccountBalAdjDetailResVo> accountBalAdjDetailResVoList = Lists.newArrayList();
//        accountBalAdjDetailResVoList.add(accountBalAdjDetailResVo);
//        pagination.setRecords(accountBalAdjDetailResVoList);
        return Message.success(accountBalAdjDetailResVoList);
    }

    @ApiOperation(value = "获取account的分红列表")
    @PostMapping("/accountAsset/accountDividendList")
    @RequiresPermissions("in:accountAsset:read")
    public Message<Page<AccountDividendResVo>> accountDividendList(@RequestBody AccountDividendReqVo accountDividendReqVo) {
        Page<AccountDividendResVo> paginationRes = new Page<>();
        List<AccountDividendResVo> accountDividendResVos = Lists.newArrayList();

        AccountDividendReqDTO accountDividendReqDTO = new AccountDividendReqDTO();
        accountDividendReqDTO.setAccountId(accountDividendReqVo.getAccountId());
        accountDividendReqDTO.setPageNo(accountDividendReqVo.getPageNo());
        accountDividendReqDTO.setPageSize(accountDividendReqVo.getPageSize());
        accountDividendReqDTO.setLikeProductCode(accountDividendReqVo.getProductCode());
        accountDividendReqDTO.setTradeStartDate(accountDividendReqVo.getTradeStartDate());
        accountDividendReqDTO.setTradeEndDate(accountDividendReqVo.getTradeEndDate());

        RpcMessage<Page<AccountDividendResDTO>> rpcMessage = transRemoteService.getAccountDividend(accountDividendReqDTO);
        if(rpcMessage.isSuccess()){
            Page<AccountDividendResDTO> pagination = rpcMessage.getContent();
            paginationRes = BeanMapperUtils.map(pagination,paginationRes.getClass());


            List<AccountDividendResDTO> accountDividendResDTOS = pagination.getRecords();
            for(AccountDividendResDTO accountDividendResDTO:accountDividendResDTOS){
                AccountDividendResVo accountDividendResVo = new AccountDividendResVo();
                accountDividendResVo.setAccountId(accountDividendResDTO.getAccountId());
                accountDividendResVo.setDividendAmount(accountDividendResDTO.getDividendAmount());
                //10是saxo的分红标记
                if(accountDividendResDTO.getCaEventTypeID().equals(10)){
                    accountDividendResVo.setDividendType(DividendTypeEnum.CASH);
                }else{
                    accountDividendResVo.setDividendType(DividendTypeEnum.SHARE);
                }
                accountDividendResVo.setProductCode(accountDividendResDTO.getProductCode());
                accountDividendResVo.setTradeDate(accountDividendResDTO.getTradeDate());

                CalDecimal<AccountDividendResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountDividendResVo);
                accountDividendResVos.add(accountDividendResVo);
            }
        }

//        AccountDividendResVo accountDividendResVo = new AccountDividendResVo();
//        accountDividendResVo.setAccountId("1234");
//        accountDividendResVo.setDividendAmount(new BigDecimal("1.234"));
//        accountDividendResVo.setDividendType(DividendTypeEnum.SHARE);
//        accountDividendResVo.setProductCode("AWS");
//        accountDividendResVo.setTradeDate(DateUtils.now());

        paginationRes.setRecords(accountDividendResVos);
        return Message.success(paginationRes);
    }

}
