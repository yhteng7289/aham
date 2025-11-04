/*package com.pivot.aham.api.web.app.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.UserAssetDTO;
import com.pivot.aham.api.server.dto.UserAssetWapperDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.web.app.dto.reqdto.GoalReqDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.vo.req.MyGoalReqVo;
import com.pivot.aham.api.web.core.ExceptionUtil;
import com.pivot.aham.api.web.app.dto.reqdto.AddGoalDTO;
import com.pivot.aham.api.web.app.dto.reqdto.DelGoalDTO;
import com.pivot.aham.api.web.app.dto.reqdto.FundMyGoalDTO;
import com.pivot.aham.api.web.app.dto.reqdto.FundMyGoalListDTO;
import com.pivot.aham.api.web.app.dto.resdto.AddGoalResDTO;
import com.pivot.aham.api.web.app.dto.resdto.DelGoalResDTO;
import com.pivot.aham.api.web.app.dto.resdto.FundMyGoalListResDTO;
import com.pivot.aham.api.web.app.dto.resdto.FundMyGoalResDTO;
import com.pivot.aham.api.web.app.dto.resdto.UserGoalDetailDTO;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.req.AddGoalReqVo;
import com.pivot.aham.api.web.app.vo.req.DelGoalReqVo;
import com.pivot.aham.api.web.app.vo.req.FundMyGoalListReqVo;
import com.pivot.aham.api.web.app.vo.req.FundMyGoalReqVo;
import com.pivot.aham.api.web.app.vo.res.AddGoalResVo;
import com.pivot.aham.api.web.app.vo.res.DelGoalResVo;
import com.pivot.aham.api.web.app.vo.res.FundMyGoalListResVo;
import com.pivot.aham.api.web.app.vo.res.FundMyGoalResVo;
import com.pivot.aham.api.web.app.vo.res.UserAssetsResVo;
import com.pivot.aham.api.web.app.vo.res.UserGoalDetailVo;
import com.pivot.aham.api.web.web.vo.res.UserAssetsDetailVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * @author YYYz
 */
/*@RestController
@RequestMapping("/api/v1/")
@Api(value = "策略目标接口", description = "策略目标接口")
public class AppGoalController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppGoalController.class);

    public static final String CURRENT_SQUIRREL_CASH_SGD = "client_squirrelCashSGD";

    @Resource
    private AppService appService;

    @Resource
    private RedissonHelper redissonHelper;

    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;

    @PostMapping("app/addGoal")
    @ApiOperation(value = "添加策略目标", produces = MediaType.APPLICATION_JSON_VALUE)
    @SentinelResource(value = "qpsFlow", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public Message<AddGoalResVo> addGoal(@RequestBody AddGoalReqVo addGoalReqVo) throws Exception {
        if (!checkLogin(addGoalReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("添加策略目标,请求参数addGoalReqVo:{}", JSON.toJSON(addGoalReqVo));
        AddGoalDTO addGoalDTO = addGoalReqVo.convertToDto(addGoalReqVo);
        LOGGER.info("添加策略目标,请求参数addGoalDTO:{}", JSON.toJSON(addGoalDTO));
        AddGoalResDTO addGoalResDTO = appService.addGoal(addGoalDTO);
        if (addGoalResDTO != null) {
            if (String.valueOf(AppResultCode.OK.value()).equals(addGoalResDTO.getResultCode())) {
                AddGoalResVo addGoalResVo = new AddGoalResVo();
                addGoalResVo.setAccountNum(addGoalResDTO.getVirtualAccount())
                        .setBankAddress("80 Raffles Place UOB Plaza 2 Singapore 048624")
                        .setBankCode("7375")
                        .setBranchCode("001")
                        .setClientId(addGoalResDTO.getClientId())
                        .setBankName("UOB")
                        .setReferCode(addGoalResDTO.getRefCode())
                        .setSwiftCode("UOVBSGSGXXX")
                        .setRecipientName("PIVOT Fintech Pte Ltd");
                return Message.success(addGoalResVo);
            } else {
                return Message.error(addGoalResDTO.getErrorMsg());
            }
        } else {
            return Message.error("request error!");
        }
    }

    @DeleteMapping("app/delGoal")
    @ApiOperation(value = "删除策略目标", produces = MediaType.APPLICATION_JSON_VALUE)
    @SentinelResource(value = "qpsFlow", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public Message<DelGoalResVo> delGoal(@RequestBody DelGoalReqVo delGoalReqVo) throws Exception {
        if (!checkLogin(delGoalReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        DelGoalDTO delGoalDTO = delGoalReqVo.convertToDto(delGoalReqVo);
        LOGGER.info("删除策略目标,请求参数delGoalReqVo:{}", JSON.toJSON(delGoalReqVo));
        DelGoalResDTO delGoalResDTO = appService.delGoal(delGoalDTO);
        if (delGoalResDTO != null) {
            if (String.valueOf(AppResultCode.OK.value()).equals(delGoalResDTO.getResultCode())) {
                DelGoalResVo delGoalResVo = new DelGoalResVo();
                delGoalResVo.setClientId(delGoalDTO.getClientId());
                delGoalResVo.setGaolId(delGoalDTO.getGoalId());
                return Message.success(delGoalResVo);
            } else {
                return Message.error(delGoalResDTO.getErrorMsg());
            }
        } else {
            return Message.error("request error!");
        }
    }

    @PostMapping("app/getTopUpCash")
    @ApiOperation(value = "获取getTopUpCash", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<AddGoalResVo> getTopUpCash() throws Exception {
        if (!checkLogin(getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        String clientId = getClientId();
        String virtualAcctNoSgd = redissonHelper.get(CURRENT_LOGIN_USER_ACCOUNT + "_" + clientId);
        AddGoalResVo addGoalResVo = new AddGoalResVo();
        addGoalResVo.setAccountNum(virtualAcctNoSgd)
                .setBankAddress("80 Raffles Place UOB Plaza 2 Singapore 048624")
                .setBankCode("7375")
                .setBranchCode("001")
                .setClientId(clientId)
                .setBankName("UOB")
                .setSwiftCode("UOVBSGSGXXX")
                .setRecipientName("PIVOT Fintech Pte Ltd");
        return Message.success(addGoalResVo);
    }

    @PostMapping("app/fundMyGoalList")
    @ApiOperation(value = "查看所有的目标", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<FundMyGoalListResVo> fundMyGoalList(@RequestBody FundMyGoalListReqVo fundMyGoalListReqVo) throws Exception {
        if (!checkLogin(fundMyGoalListReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        UserAssetsResVo userAssetsResVo = new UserAssetsResVo();
        UserAssetDTO userAssetDTO = new UserAssetDTO();
        userAssetDTO.setClientId(fundMyGoalListReqVo.getClientId());
        RpcMessage<UserAssetWapperDTO> assetWapperDTORpcMessage = assetServiceRemoteService.queryUserAssets(userAssetDTO);
        if (RpcMessageStandardCode.OK.value() == assetWapperDTORpcMessage.getResultCode()) {
            UserAssetWapperDTO userAssetWapperDTO = assetWapperDTORpcMessage.getContent();
            userAssetsResVo = BeanMapperUtils.map(userAssetWapperDTO, UserAssetsResVo.class);
        }

        LOGGER.info("添加策略目标,请求参数fundMyGoalListReqVo:{}", JSON.toJSON(fundMyGoalListReqVo));
        FundMyGoalListDTO fundMyGoalListDTO = fundMyGoalListReqVo.convertToDto(fundMyGoalListReqVo);
        FundMyGoalListResDTO fundMyGoalListResDTO = appService.fundMyGoalList(fundMyGoalListDTO);
        if (fundMyGoalListResDTO.getResultCode().equals(String.valueOf(AppResultCode.OK))) {
            FundMyGoalListResVo fundMyGoalListResVo = new FundMyGoalListResVo();
            fundMyGoalListResVo.setUserGoalDetailList(convertToVo(fundMyGoalListResDTO.getFundMyGoals(), userAssetsResVo))
                    .setClientId(fundMyGoalListResDTO.getClientId())
                    .setSquirrelCashSGD(redissonHelper.get(CURRENT_SQUIRREL_CASH_SGD + "_" + fundMyGoalListReqVo.getClientId()));
            return Message.success(fundMyGoalListResVo);
        } else {
            return Message.error(fundMyGoalListResDTO.getErrorMsg());
        }
    }

    @PostMapping("app/fundMyGoal")
    @ApiOperation(value = "查看目标", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<List<FundMyGoalResVo>> fundMyGoal(@RequestBody FundMyGoalReqVo fundMyGoalReqVo) throws Exception {
        if (!checkLogin(getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        if (CollectionUtils.isNotEmpty(fundMyGoalReqVo.getMyGoalStrReqVos())) {
            List<MyGoalReqVo> list = Lists.newArrayList();
            for (String listJson : fundMyGoalReqVo.getMyGoalStrReqVos()) {
                MyGoalReqVo myGoalReqVo = JSON.parseObject(listJson, MyGoalReqVo.class);
                list.add(myGoalReqVo);
            }
            fundMyGoalReqVo.setMyGoalReqVos(list);
        }
        LOGGER.info("添加策略目标,请求参数fundMyGoalReqVo:{}", JSON.toJSON(fundMyGoalReqVo));
        FundMyGoalDTO fundMyGoalDTO = fundMyGoalReqVo.convertToDto(fundMyGoalReqVo);
        List<FundMyGoalResVo> resVoList = Lists.newArrayList();
        for (GoalReqDTO goalReqDTO : fundMyGoalDTO.getGoalReqDTOS()) {
            FundMyGoalResDTO fundMyGoalResDTO = appService.fundMyGoal(goalReqDTO);
            if (fundMyGoalResDTO != null) {
                FundMyGoalResVo fundMyGoalResVo = new FundMyGoalResVo();
                fundMyGoalResVo.setApplymoney(fundMyGoalResDTO.getApplyMoney())
                        .setClientId(fundMyGoalResDTO.getClientId())
                        .setDate(fundMyGoalResDTO.getDate())
                        .setGoalId(fundMyGoalResDTO.getGoalId())
                        .setRisk(goalReqDTO.getPortfolioId().substring(3, 4))
                        .setPortfolioId(goalReqDTO.getPortfolioId())
                        .setGoalNo(goalReqDTO.getGoalNo())
                        .setGoalName(fundMyGoalResDTO.getGoalName())
                        .setOrderId(fundMyGoalResDTO.getOrderId())
                        .setTime(fundMyGoalResDTO.getTime());
                resVoList.add(fundMyGoalResVo);
            }
        }
        return Message.success(resVoList);
    }

    public List<UserGoalDetailVo> convertToVo(List<UserGoalDetailDTO> userBankDetailResDTOList, UserAssetsResVo userAssetsResVo) {
        List<UserGoalDetailVo> userGoalDetailVos = Lists.newArrayList();
        for (UserGoalDetailDTO userGoalDetailDTO : userBankDetailResDTOList) {
            if (!userGoalDetailDTO.getPortfolioId().isEmpty()) {
                UserGoalDetailVo userGoalDetailVo = new UserGoalDetailVo();
                userGoalDetailVo.setFrequency(userGoalDetailDTO.getFrequency())
                        .setGoalId(userGoalDetailDTO.getGoalId())
                        .setGoalName(userGoalDetailDTO.getGoalName())
                        .setGoalNo(userGoalDetailDTO.getGoalNo())
                        .setPortfolioId(userGoalDetailDTO.getPortfolioId())
                        .setRisk(userGoalDetailDTO.getPortfolioId().substring(3, 4));
                if (userGoalDetailDTO.getCurrentAssetValue().equals("") || userGoalDetailDTO.getCurrentAssetValue() == null) {
                    userGoalDetailVo.setCurrentAssetValue(BigDecimal.ZERO);
                } else {
                    List<UserAssetsDetailVo> userAssetsDetailVoList = userAssetsResVo.getAssetDetails();
                    for (UserAssetsDetailVo userAssetsDetailVo : userAssetsDetailVoList) {
                        if (userAssetsDetailVo.getGoalId().toLowerCase().equalsIgnoreCase(userGoalDetailDTO.getGoalId().toLowerCase())) {
                            // Grabbing the asset value from DAAS instead of FE
                            // Previouslly from FE. userGoalDetailDTO.getCurrentAssetValue()
                            userGoalDetailVo.setCurrentAssetValue(userAssetsDetailVo.getAssetValueSGD());
                            break;
                        }
                    }
                }
                
                if (userGoalDetailDTO.getSuggestAmt().equals("") || userGoalDetailDTO.getSuggestAmt() == null) {
                    userGoalDetailVo.setSuggestAmt(BigDecimal.ZERO);
                } else {
                    userGoalDetailVo.setSuggestAmt(new BigDecimal(userGoalDetailDTO.getSuggestAmt()));
                }
                userGoalDetailVos.add(userGoalDetailVo);
            }
        }
        return userGoalDetailVos;
    }

}
*/