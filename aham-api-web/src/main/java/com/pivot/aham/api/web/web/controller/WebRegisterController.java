package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountResDTO;
import com.pivot.aham.api.server.dto.UserBaseInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.dto.UserSetGoalMoneyDTO;
import com.pivot.aham.api.server.dto.UserSetGoalMoneyResDTO;
import com.pivot.aham.api.server.dto.UserStaticsReqDTO;
import com.pivot.aham.api.server.dto.UserStaticsResDTO;
import com.pivot.aham.api.server.dto.resp.ReceivedTransferItem;
import com.pivot.aham.api.server.remoteservice.UobTradeRemoteService;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.UserStaticsRemoteService;
import com.pivot.aham.api.web.in.controller.CalDateSupport;
import com.pivot.aham.api.web.web.vo.UserGoalInfoReqVo;
import com.pivot.aham.api.web.web.vo.UserRegisterReqVo;
import com.pivot.aham.api.web.web.vo.UserSetGoalMoneyReqVo;
import com.pivot.aham.api.web.web.vo.req.DeleteUserGoalInfoReqVo;
import com.pivot.aham.api.web.web.vo.req.UpdateUserGoalInfoReqVo;
import com.pivot.aham.api.web.web.vo.req.UpdateUserRegisterReqVo;
import com.pivot.aham.api.web.web.vo.res.UserSetGoalMoneyResVo;

import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.enums.CurrencyEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author luyang.li
 * @date 18/11/30
 * <p>
 * 提供个FE的接口 -- 用户注册之后FE审核通过,给用户发送邮件的时候同步用户信息掉该接口
 */
@RestController
@RequestMapping("/app/")
@Api(value = "注册用户信息同步接口", description = "注册用户信息同步接口")
public class WebRegisterController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRegisterController.class);

    @Resource
    private UserServiceRemoteService userServiceRemoteService;

    @Resource
    private UserStaticsRemoteService userStaticsRemoteService;

    @Resource
    private UobTradeRemoteService uobTradeRemoteService;

    @PostMapping("user/baseInfo.api")
    @ApiOperation(value = "用户注册信息同步", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下4个参数：\n" + "1.用户clientId\n" + "2. clientName\n" + "3. virtualAccountNo\n" + "4. currentType")
    public Message<Void> userBaseInfo(@RequestBody @Valid UserRegisterReqVo userRegisterVo) throws Exception {
        LOGGER.info("用户注册信息同步,请求参数userRegisterVo:{}", JSON.toJSON(userRegisterVo));
        List<UserBaseInfoDTO> userBaseInfoDTOs = userRegisterVo.convertToDto();
        RpcMessage<String> statusSave = userServiceRemoteService.saveUserBaseInfos(userBaseInfoDTOs); // Edit By WooiTatt
        LOGGER.info("用户注册信息同步,完成,userRegisterVo:{}", JSON.toJSON(userRegisterVo));
        if (statusSave.isSuccess()) {
            return Message.success("Successfully");
        } else {
            return Message.error(statusSave.getErrMsg());
        }
    }

    @PostMapping("user/updateBaseInfo.api")
    @ApiOperation(value = "修改用户信息", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下4个参数：\n" + "1.用户clientId\n" + "2. clientName\n" + "3. virtualAccountNo\n" + "4. currentType")
    public Message<Void> updateUserBaseInfo(@RequestBody @Valid UpdateUserRegisterReqVo userRegisterVo) throws Exception {
        LOGGER.info("用户注册信息同步,请求参数userRegisterVo:{}", JSON.toJSON(userRegisterVo));
        List<UserBaseInfoDTO> userBaseInfoDTOs = userRegisterVo.convertToDto();
        userServiceRemoteService.updateUserBaseInfos(userBaseInfoDTOs);
        LOGGER.info("用户注册信息同步,完成,userRegisterVo:{}", JSON.toJSON(userRegisterVo));
        return Message.success();
    }

    @PostMapping("user/goalInfo.api")
    @ApiOperation(value = "用户goal信息同步", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下6个参数：\n" + "1.投资目标:goalId\n" + "2.方案标识:portfolioId\n" + "3.风险等级:riskLevel\n"
            + "4.银行转账使用的code:referenceCode\n" + "5.用户年龄:ageLevel\n" + "6.clientId")
    public Message<Void> userGoalInfo(@RequestBody @Valid UserGoalInfoReqVo goalInfoReqVo) {
        LOGGER.info("用户goal信息同步,请求参数:goalInfoReqVo:{}", JSON.toJSON(goalInfoReqVo));
        List<UserGoalInfoDTO> userGoalInfoDTOs = goalInfoReqVo.convertToDto();
        userServiceRemoteService.saveUserGoalInfos(userGoalInfoDTOs);
        LOGGER.info("用户goal信息同步,完成,goalInfoReqVo:{}", JSON.toJSON(goalInfoReqVo));
        return Message.success();
    }

    @PostMapping("user/updateGoalInfo.api")
    @ApiOperation(value = "用户goal信息修改", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下6个参数：\n" + "1.投资目标:goalId\n" + "2.方案标识:portfolioId\n" + "3.风险等级:riskLevel\n"
            + "4.银行转账使用的code:referenceCode\n" + "5.用户年龄:ageLevel\n" + "6.clientId")
    public Message<Void> updateGoalInfo(@RequestBody @Valid UpdateUserGoalInfoReqVo goalInfoReqVo) {
        LOGGER.info("用户goal信息同步,请求参数:goalInfoReqVo:{}", JSON.toJSON(goalInfoReqVo));
        List<UserGoalInfoDTO> userGoalInfoDTOs = goalInfoReqVo.convertToDto();
        userServiceRemoteService.updateUserGoalInfos(userGoalInfoDTOs);
        LOGGER.info("用户goal信息同步,完成,goalInfoReqVo:{}", JSON.toJSON(goalInfoReqVo));
        return Message.success();
    }

    @PostMapping("user/deletegoal.api")
    @ApiOperation(value = "用户删除目标策略", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<Void> deleteGoalInfo(@RequestBody @Valid DeleteUserGoalInfoReqVo deleteUserGoalInfoReqVo) {
        LOGGER.info("用户删除目标策略,请求参数:deleteUserGoalInfoReqVo:{}", JSON.toJSON(deleteUserGoalInfoReqVo));
        String clientId = deleteUserGoalInfoReqVo.getClientId();
        String goalId = deleteUserGoalInfoReqVo.getGoalsId();
        String referenceCode = "";
        //查询每个client下的所有goal
        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
        userGoalInfoDTO.setClientId(clientId);
        userGoalInfoDTO.setGoalId(goalId);
        RpcMessage<List<UserGoalInfoResDTO>> rpcMessageUserGoal = userServiceRemoteService.getUserGoalInfoList(userGoalInfoDTO);
        logger.info("rpcMessageUserGoal {} ", rpcMessageUserGoal);
        List<UserGoalInfoResDTO> userGoalInfoResDTOList = rpcMessageUserGoal.getContent();
        if (userGoalInfoResDTOList.isEmpty()) {
            return Message.error(500, "Goal " + goalId + " doesn't exist");
        }
        for (UserGoalInfoResDTO userGoalInfoResDTO : userGoalInfoResDTOList) {
            // Always get 1 records because goal ID , client ID, reference code always unique
            UserStaticsReqDTO userStaticsReqDTO = new UserStaticsReqDTO();
            userStaticsReqDTO.setClientId(userGoalInfoResDTO.getClientId());
            userStaticsReqDTO.setGoalId(userGoalInfoResDTO.getGoalId());
            userStaticsReqDTO.setStaticDate(CalDateSupport.getCalYesDate());

            referenceCode = userGoalInfoResDTO.getReferenceCode();
            RpcMessage<List<UserStaticsResDTO>> rpcMessage_userStatics
                    = userStaticsRemoteService.getUserStatics(userStaticsReqDTO);
            logger.info("rpcMessage_userStatics {} ", rpcMessage_userStatics);
            if (rpcMessage_userStatics.isSuccess()) {
                List<UserStaticsResDTO> userStaticsResDTOList = rpcMessage_userStatics.getContent();
                if (userStaticsResDTOList.size() > 0) {
                    UserStaticsResDTO userStaticsRes = userStaticsResDTOList.get(0);
                    BigDecimal assetValueSgd = userStaticsRes.getAdjFundAssetInSgd();
                    if (assetValueSgd.compareTo(new BigDecimal(0)) > 0) {
                        return Message.error(500, "Goal " + goalId + " has asset balance");
                    }
                }
            }
        }
        //按virtualaccount统计squirrelsave的open和close
        //按client获取所有虚拟账户
        BankVirtualAccountDTO bankVirtualAccount = new BankVirtualAccountDTO();
        bankVirtualAccount.setClientId(clientId);
        bankVirtualAccount.setCurrency(CurrencyEnum.SGD);

        List<BankVirtualAccountResDTO> bankVirtualAccountList = userServiceRemoteService.queryListBankVirtualAccount(bankVirtualAccount);
        logger.info("bankVirtualAccountList {} ", bankVirtualAccountList);
        if (bankVirtualAccountList != null) {
            for (BankVirtualAccountResDTO virtualAccount : bankVirtualAccountList) {
                if (virtualAccount.getCurrency().getCode().equalsIgnoreCase("SGD")) {
                    String sgdVirtualAccount = virtualAccount.getVirtualAccountNo();
                    RpcMessage<List<ReceivedTransferItem>> rpcReceivedTransferItem
                            = uobTradeRemoteService.queryProcessingByVirtualAccountNo(sgdVirtualAccount, referenceCode);
                    logger.info("rpcReceivedTransferItem {} ", rpcReceivedTransferItem);
                    if (rpcReceivedTransferItem.isSuccess()) {
                        List<ReceivedTransferItem> receivedTransferItemList = rpcReceivedTransferItem.getContent();
                        if (receivedTransferItemList.size() > 0) {
                            return Message.error(500, "Goal " + goalId + " has pending balance");
                        }
                    }
                }
            }
        }

        userServiceRemoteService.updateDeletedByClientIdAndGoalId(clientId, goalId);
        LOGGER.info("用户goal信息同步,完成,deleteUserGoalInfoReqVo:{}", JSON.toJSON(deleteUserGoalInfoReqVo));
        return Message.success();
    }

    @PostMapping("user/setGoalMoney.api")
    @ApiOperation(value = "用户设置goal金额信息", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下4个参数：\n" + "1.投资目标:goalId\n " + "2.银行转账使用的code:referenceCode\n"
            + "3.clientId\n " + "4.money")
    public Message<List<UserSetGoalMoneyResVo>> usersetGoalMoney(@RequestBody
            @Valid UserSetGoalMoneyReqVo setGoalMoneyReqVo, HttpServletRequest request,
                                                                 HttpServletResponse response) {
        LOGGER.info("用户设置goal金额信息,请求参数:setGoalMoneyReqVo:{}", JSON.toJSON(setGoalMoneyReqVo));
        List<UserSetGoalMoneyDTO> userSetGoalMoneyDTOs = setGoalMoneyReqVo.convertToDto();
        List<UserSetGoalMoneyResDTO> userSetGoalMoneyResDTOs = userServiceRemoteService.userSetGoalMoney(userSetGoalMoneyDTOs);

        List<UserSetGoalMoneyResVo> setGoalMoneyResVos = userSetGoalMoneyResDTOs.stream().map(item -> {
            UserSetGoalMoneyResVo vo = new UserSetGoalMoneyResVo();
            vo.setGoalsId(item.getGoalId());
            vo.setClientId(item.getClientId());
            vo.setMoney(item.getMoney());
            vo.setTransNo(item.getTransNo());
            return vo;
        }
        ).collect(Collectors.toList());
        LOGGER.info("用户设置goal金额信息,完成,setGoalMoneyReqVo:{}", JSON.toJSON(setGoalMoneyResVos));
        return Message.success(setGoalMoneyResVos);
    }

}
