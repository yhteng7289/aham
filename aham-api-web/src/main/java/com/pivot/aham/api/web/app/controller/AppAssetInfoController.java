/*package com.pivot.aham.api.web.app.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.pivot.aham.api.server.dto.UserAssetDTO;
import com.pivot.aham.api.server.dto.UserAssetWapperDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.web.app.dto.reqdto.FundMyGoalListDTO;
import com.pivot.aham.api.web.app.dto.resdto.FundMyGoalListResDTO;
import com.pivot.aham.api.web.app.dto.resdto.GoalDetailResDTO;
import com.pivot.aham.api.web.app.dto.resdto.UserGoalDetailDTO;
import com.pivot.aham.api.web.app.febase.AppResultCode;
import com.pivot.aham.api.web.app.service.AppService;
import com.pivot.aham.api.web.app.vo.req.DepositReqVo;
import com.pivot.aham.api.web.app.vo.req.UserAssetsReqVo;
import com.pivot.aham.api.web.app.vo.res.DepositResVo;
import com.pivot.aham.api.web.web.vo.res.AssetValueVo;
import com.pivot.aham.api.web.web.vo.res.UserAssetsDetailVo;
import com.pivot.aham.api.web.web.vo.res.UserAssetsResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.RiskLevelEnum;
import com.pivot.aham.common.enums.app.FrequencyEnum;
import com.pivot.aham.common.enums.app.GoalTypeEnum;
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

/**
 * @author YYYz
 */
/*@RestController
@RequestMapping("/api/v1/")
@Api(value = "资产信息", description = "资产信息接口")
public class AppAssetInfoController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppAssetInfoController.class);

    public static final String CURRENT_SQUIRREL_CASH_SGD = "client_squirrelCashSGD";

    public static final String CURRENT_LOGIN_USER_PORTFOLIOID = "current_user_portfolioId";

    public static final String CURRENT_LOGIN_USER_GOALLIST = "current_user_goalList";

    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;
    @Resource
    private AppService appService;
    @Resource
    private RedissonHelper redissonHelper;

    @PostMapping("app/assets")
    @ApiOperation(value = "用户资产", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "注册接口需要以下1个参数：\n" + "1.用户clientId\n")
    public Message<UserAssetsResVo> userBaseInfo(@RequestBody UserAssetsReqVo userAssetsReqVo) throws Exception {
        if (!checkLogin(userAssetsReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户资产,请求参数userAssetsReqVo:{}", JSON.toJSON(userAssetsReqVo));
        UserAssetDTO userAssetDTO = userAssetsReqVo.convertToDto(userAssetsReqVo);
        String listJson = redissonHelper.get(CURRENT_LOGIN_USER_GOALLIST + "_" + userAssetsReqVo.getClientId());
        List<GoalDetailResDTO> goals = JSON.parseObject(listJson, new TypeReference<List<GoalDetailResDTO>>() {
        });
        UserAssetsResVo userAssetsResVo = new UserAssetsResVo();
        RpcMessage<UserAssetWapperDTO> assetWapperDTORpcMessage = assetServiceRemoteService.queryUserAssets(userAssetDTO);
        if (RpcMessageStandardCode.OK.value() == assetWapperDTORpcMessage.getResultCode()) {
            UserAssetWapperDTO userAssetWapperDTO = assetWapperDTORpcMessage.getContent();
            userAssetsResVo = BeanMapperUtils.map(userAssetWapperDTO, UserAssetsResVo.class);
        }
        LOGGER.info("userAssetsResVo {} ", userAssetsResVo);

        redissonHelper.set(CURRENT_SQUIRREL_CASH_SGD + "_" + userAssetsReqVo.getClientId(), userAssetsResVo.getSquireelCashSGD(), 3600);
        FundMyGoalListDTO fundMyGoalListDTO = new FundMyGoalListDTO();
        fundMyGoalListDTO.setClientId(userAssetsResVo.getClientId());
        FundMyGoalListResDTO fundMyGoalListResDTO = appService.fundMyGoalList(fundMyGoalListDTO);
        List<UserAssetsDetailVo> lists = Lists.newArrayList();
        LOGGER.info("fundMyGoalListResDTO {} ", fundMyGoalListResDTO);
        for (UserGoalDetailDTO userGoalDetailDTO : fundMyGoalListResDTO.getFundMyGoals()) {
            if (CollectionUtils.isNotEmpty(userAssetsResVo.getAssetDetails())) {
                boolean isMatch = false;
                for (UserAssetsDetailVo userAssetsDetailVo : userAssetsResVo.getAssetDetails()) {
                    if (userAssetsDetailVo.getGoalId().equals(userGoalDetailDTO.getGoalId())) {
                        if (!userGoalDetailDTO.getType().isEmpty()) {
                            userAssetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getType().replaceAll(" ", "")).getValue()));
                        } else {
                            userAssetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getGoalName().replaceAll(" ", "")).getValue()));
                        }
                        // Special Handling for goal type = education, if need to split the goal name to get the child. Briliant !
                        if (userAssetsDetailVo.getGoalType().equalsIgnoreCase("2")) {
                            // For Example : EDUCATIONSAVE (Buddy) 
                            try {
                                String childName = userGoalDetailDTO.getGoalName().split("\\s+")[1].replace("(", "").replace(")", "");
                                userAssetsDetailVo.setChildName(childName);
                            } catch (Exception e) {
                                throw new BusinessException("Unable to set childName on app/assets API. goalName : " + userGoalDetailDTO.getGoalName());
                            }
                        }

                        // Added frequency field;
                        String frequencyDes = toInitCap(userGoalDetailDTO.getFrequency().toLowerCase());
                        // Spegatti coding on FE side. 
                        if (frequencyDes.equals("Year")) {
                            frequencyDes = "Yearly";
                        }
                        String frequencyValue = String.valueOf(FrequencyEnum.forDesc(frequencyDes.replaceAll(" ", "")).getValue());
                        userAssetsDetailVo.setFrequency(frequencyValue);
                        userAssetsDetailVo.setGoalId(userGoalDetailDTO.getGoalId());
                        userAssetsDetailVo.setGoalName(userGoalDetailDTO.getGoalName());
                        logger.info("userAssetsDetailVo.getAssetValueSGD() {} ", userAssetsDetailVo.getAssetValueSGD());
                        if (userAssetsDetailVo.getAssetValueSGD().compareTo(BigDecimal.ZERO) <= 0) {
                            userAssetsDetailVo.setAssetValueSGD(new BigDecimal(userGoalDetailDTO.getCurrentAssetValue()));
                        }
                        if (userGoalDetailDTO.getPortfolioId().equals("")) {
                            String portfolioId = redissonHelper.get(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + userAssetsReqVo.getClientId());
                            userAssetsDetailVo.setPortfolioId(portfolioId);
                        } else {
                            userAssetsDetailVo.setPortfolioId(userGoalDetailDTO.getPortfolioId());
                        }
                        
                        lists.add(userAssetsDetailVo);
                        isMatch = true;
                        
                        break;
                    }
                }

                if (!isMatch) {
                    UserAssetsDetailVo assetsDetailVo = new UserAssetsDetailVo();
                    assetsDetailVo.setPortfolioReturn(BigDecimal.ZERO)
                            .setTotalReturnSGD(BigDecimal.ZERO)
                            .setFxImpactSGD(BigDecimal.ZERO);

                    if (!userGoalDetailDTO.getType().isEmpty()) {
                        assetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getType().replaceAll(" ", "")).getValue()));
                    } else {
                        assetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getGoalName().replaceAll(" ", "")).getValue()));
                    }
                    assetsDetailVo.setAssetValueSGD(BigDecimal.ZERO)
                            .setGoalId(userGoalDetailDTO.getGoalId())
                            .setInvestedAmountSGD(BigDecimal.ZERO)
                            .setTargetMoney(BigDecimal.ZERO)
                            .setTotalDepositSGD(BigDecimal.ZERO)
                            .setTotalWithdrawalSGD(BigDecimal.ZERO);
                    if (userGoalDetailDTO.getPortfolioId().equals("")) {
                        String portfolioId = redissonHelper.get(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + userAssetsReqVo.getClientId());
                        assetsDetailVo.setPortfolioId(portfolioId);
                    } else {
                        assetsDetailVo.setPortfolioId(userGoalDetailDTO.getPortfolioId());
                    }

                    // Special Handling for goal type = education, if need to split the goal name to get the child. Briliant !
                    if (assetsDetailVo.getGoalType().equalsIgnoreCase("2")) {
                        // For Example : EDUCATIONSAVE (Buddy) 
                        try {
                            String childName = userGoalDetailDTO.getGoalName().split("\\s+")[1].replace("(", "").replace(")", "");
                            assetsDetailVo.setChildName(childName);
                        } catch (Exception e) {
                            throw new BusinessException("Unable to set childName on app/assets API. goalName : " + userGoalDetailDTO.getGoalName());
                        }
                    }
                    // Added frequency field;
                    String frequencyDes = toInitCap(userGoalDetailDTO.getFrequency().toLowerCase());
                    // Spegatti coding on FE side. 
                    if (frequencyDes.equals("Year")) {
                        frequencyDes = "Yearly";
                    }
                    String frequencyValue = String.valueOf(FrequencyEnum.forDesc(frequencyDes.replaceAll(" ", "")).getValue());
                    assetsDetailVo.setFrequency(frequencyValue);
                    assetsDetailVo.setGoalId(userGoalDetailDTO.getGoalId());
                    assetsDetailVo.setGoalName(userGoalDetailDTO.getGoalName());
                    if (userGoalDetailDTO.getPortfolioId().equals("")) {
                        String portfolioId = redissonHelper.get(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + userAssetsReqVo.getClientId());
                        assetsDetailVo.setPortfolioId(portfolioId);
                    } else {
                        assetsDetailVo.setPortfolioId(userGoalDetailDTO.getPortfolioId());
                    }               
                    lists.add(assetsDetailVo);
                }
            } else {
                UserAssetsDetailVo assetsDetailVo = new UserAssetsDetailVo();
                assetsDetailVo.setPortfolioReturn(BigDecimal.ZERO)
                        .setTotalReturnSGD(BigDecimal.ZERO)
                        .setFxImpactSGD(BigDecimal.ZERO);
                LOGGER.info("userGoalDetailDTO {} ", userGoalDetailDTO);
                if (!userGoalDetailDTO.getType().isEmpty()) {
                    assetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getType().replaceAll(" ", "")).getValue()));
                } else {
                    assetsDetailVo.setGoalType(String.valueOf(GoalTypeEnum.forDesc(userGoalDetailDTO.getGoalName().replaceAll(" ", "")).getValue()));
                }
                assetsDetailVo.setAssetValueSGD(BigDecimal.ZERO)
                        .setGoalId(userGoalDetailDTO.getGoalId())
                        .setInvestedAmountSGD(BigDecimal.ZERO)
                        .setTargetMoney(BigDecimal.ZERO)
                        .setTotalDepositSGD(BigDecimal.ZERO)
                        .setTotalWithdrawalSGD(BigDecimal.ZERO);
                if (userGoalDetailDTO.getPortfolioId().equals("")) {
                    String portfolioId = redissonHelper.get(CURRENT_LOGIN_USER_PORTFOLIOID + "_" + userAssetsReqVo.getClientId());
                    assetsDetailVo.setPortfolioId(portfolioId);
                } else {
                    assetsDetailVo.setPortfolioId(userGoalDetailDTO.getPortfolioId());
                }
                if (assetsDetailVo.getGoalType().equalsIgnoreCase("2")) {
                    // For Example : EDUCATIONSAVE (Buddy) 
                    try {
                        String childName = userGoalDetailDTO.getGoalName().split("\\s+")[1].replace("(", "").replace(")", "");
                        assetsDetailVo.setChildName(childName);
                    } catch (Exception e) {
                        throw new BusinessException("Unable to set childName on app/assets API. goalName : " + userGoalDetailDTO.getGoalName());
                    }
                }
                // Added frequency field;
                String frequencyDes = toInitCap(userGoalDetailDTO.getFrequency().toLowerCase());
                // Spegatti coding on FE side. 
                if (frequencyDes.equals("Year")) {
                    frequencyDes = "Yearly";
                }
                String frequencyValue = String.valueOf(FrequencyEnum.forDesc(frequencyDes.replaceAll(" ", "")).getValue());
                assetsDetailVo.setGoalId(userGoalDetailDTO.getGoalId());
                assetsDetailVo.setGoalName(userGoalDetailDTO.getGoalName());
                assetsDetailVo.setFrequency(frequencyValue);
                lists.add(assetsDetailVo);
            }
        }
        userAssetsResVo.setAssetDetails(lists);
        if (CollectionUtils.isNotEmpty(userAssetsResVo.getAssetDetails())) {
            for (UserAssetsDetailVo userAssetsDetailVo : userAssetsResVo.getAssetDetails()) {
                for (GoalDetailResDTO goalDetailResDTO : goals) {
                    if (goalDetailResDTO.getGoalId().equals(userAssetsDetailVo.getGoalId())) {
                        if (userAssetsDetailVo.getTotalReturnSGD() == null) {
                            userAssetsDetailVo.setTotalReturnSGD(BigDecimal.ZERO);
                        }
                        if (userAssetsDetailVo.getPortfolioReturn() == null) {
                            userAssetsDetailVo.setPortfolioReturn(BigDecimal.ZERO);
                        }
                        if (userAssetsDetailVo.getFxImpactSGD() == null) {
                            userAssetsDetailVo.setFxImpactSGD(BigDecimal.ZERO);
                        }
                        userAssetsDetailVo.setRiskLevel(RiskLevelEnum.forValue(Integer.valueOf(userAssetsDetailVo.getPortfolioId().substring(3, 4))).getAppName());
                        userAssetsDetailVo.setTargetMoney(new BigDecimal(goalDetailResDTO.getRecommended()));
                        List<AssetValueVo> assetValueVos = Lists.newArrayList();
                        int minValueSize = (goalDetailResDTO.getValue1().size() > goalDetailResDTO.getValue2().size()) ? goalDetailResDTO.getValue2().size() : goalDetailResDTO.getValue1().size();
                        int minSize = (goalDetailResDTO.getValue1().size() > minValueSize) ? minValueSize : goalDetailResDTO.getValue1().size();
                        for (int i = 0; i < minSize; i++) {
                            AssetValueVo assetValueVo = new AssetValueVo();
                            assetValueVo.setDate(goalDetailResDTO.getChartDate().get(i).getDateValue());
                            assetValueVo.setNetDeposit(new BigDecimal(goalDetailResDTO.getValue2().get(i).getValue2()));
                            assetValueVo.setAssetValue(new BigDecimal(goalDetailResDTO.getValue1().get(i).getValue1()));
                            assetValueVos.add(assetValueVo);
                        }
                        userAssetsDetailVo.setAssetValueVos(assetValueVos);
                    }
                }
            }
        }
        LOGGER.info("用户资产,完成,UserAssetsResVo:{}", JSON.toJSON(userAssetsResVo));
        return Message.success(userAssetsResVo);
    }

    @PostMapping("app/deposit")
    @ApiOperation(value = "追加投资", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<DepositResVo> deposit(@RequestBody DepositReqVo depositReqVo) throws Exception {
        if (!checkLogin(depositReqVo.getClientId())) {
            return Message.error(AppResultCode.UNAUTHORIZED.value(), AppResultCode.UNAUTHORIZED.msg());
        }
        LOGGER.info("用户资产,deposit:{}", JSON.toJSON(depositReqVo));
        FundMyGoalListDTO fundMyGoalListDTO = new FundMyGoalListDTO();
        fundMyGoalListDTO.setClientId(depositReqVo.getClientId());
        FundMyGoalListResDTO fundMyGoalListResDTO = appService.fundMyGoalList(fundMyGoalListDTO);
        BigDecimal recommendTransFerAmt = BigDecimal.ZERO;
        for (UserGoalDetailDTO userGoalDetailDTO : fundMyGoalListResDTO.getFundMyGoals()) {
            if (userGoalDetailDTO.getGoalId().equals(depositReqVo.getGoalId())) {
                recommendTransFerAmt = new BigDecimal(userGoalDetailDTO.getSuggestAmt());
            }
        }
        DepositResVo depositResVo = new DepositResVo();
        depositResVo.setRecommendTransFerAmt(recommendTransFerAmt).setAccountNum(redissonHelper.get(DigestUtil.md5Hex(depositReqVo.getClientId())))
                .setReferCode("QQFJK14295").setRecipientName("PIVOT Fintech Pte Ltd").setBankName("UOB").setBankAddress("80 Raffles Place UOB Plaza 2 Singapore 048624").setBankCode("7375")
                .setBranchCode("001").setBankName("UOB")
                .setSwiftCode("UOVBSGSGXXX");
        return Message.success(depositResVo);
    }

    private static String toInitCap(String param) {
        if (param != null && param.length() > 0) {
            char[] charArray = param.toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            // set capital letter to first position
            return new String(charArray);
            // return desired output
        } else {
            return "";
        }
    }
}
*/