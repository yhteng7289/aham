package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.BeforeGameTestDTO;
import com.pivot.aham.api.server.dto.VersionInfoDTO;
import com.pivot.aham.api.server.dto.VersionInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserTestRemoteService;
import com.pivot.aham.api.service.mapper.model.RiskRegulationPO;
import com.pivot.aham.api.service.service.RiskRegulationService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by senyang.zheng on 19/04/15
 * <p>
 * 风测相关
 */
@Slf4j
@Service(interfaceClass = UserTestRemoteService.class)
public class UserTestRemoteServiceImpl implements UserTestRemoteService {

    @Resource
    private RiskRegulationService regulationService;

    @Override
    public RpcMessage<String> getPortfolioId(BeforeGameTestDTO beforeGameTestDTO) {
        if (checkParam(beforeGameTestDTO)) {
            //风测分值计算公式 (汇总分值-10)/43 * 5
            BigDecimal quesPartScore = calculateQuesPartScore(beforeGameTestDTO).multiply(new BigDecimal("0.6"));
            BigDecimal gamePartScore = calculateGamePartScore(beforeGameTestDTO).multiply(new BigDecimal("0.4"));
            if (gamePartScore == null) {
                return RpcMessage.error("params error！！！");
            }
            BigDecimal totalScore = quesPartScore.add(gamePartScore).setScale(0, BigDecimal.ROUND_HALF_UP);
            RiskRegulationPO queryPO = new RiskRegulationPO();
            queryPO.setAgeLevel(Integer.valueOf(beforeGameTestDTO.getAge()));
            queryPO.setPoolLevel(Integer.valueOf(beforeGameTestDTO.getHorizon()));
            queryPO.setTotalScore(totalScore.intValue());
            RiskRegulationPO regulationPO = regulationService.queryByPO(queryPO);
            String portfolioId = "P" + beforeGameTestDTO.getHorizon() + "R" + regulationPO.getRiskLevel() + "A" + beforeGameTestDTO.getAge();
            return RpcMessage.success(portfolioId);
        } else {
            return RpcMessage.error("params error！！！");
        }
    }

    @Override
    public RpcMessage<VersionInfoResDTO> getVersionInfo(VersionInfoDTO versionInfoDTO) {
        VersionInfoResDTO versionInfoResDTO = new VersionInfoResDTO();
        if (versionInfoDTO.getClientId() == 0) {
            if (999 > versionInfoDTO.getVersion()) {
                versionInfoResDTO.setDownloadUrl("https://itunes.apple.com/us/app/xuan-ji-zhi-tou/id1139537328?l=zh&ls=1&mt=8");
                versionInfoResDTO.setForcedUpdate("0");
                versionInfoResDTO.setNewVersion("9999999");
                versionInfoResDTO.setUpdateMessage("1.fix some bugs\n"+"2.fix some bugs\n");
                return RpcMessage.success(versionInfoResDTO);
            }
        } else {
            if (999 > versionInfoDTO.getVersion()) {
                versionInfoResDTO.setDownloadUrl("https://ljweb.hongdianfund.com/app/4/6024300/merak_Android/merak-6024300-38-merak_Android.apk");
                versionInfoResDTO.setForcedUpdate("0");
                versionInfoResDTO.setNewVersion("9999999");
                versionInfoResDTO.setUpdateMessage("1.fix some bugs\n"+"2.fix some bugs\n");
                return RpcMessage.success(versionInfoResDTO);
            }
        }
        return RpcMessage.success(versionInfoResDTO);
    }

    private BigDecimal calculateGamePartScore(BeforeGameTestDTO beforeGameTestDTO) {
        List<Integer> answers = beforeGameTestDTO.getQuestionsAndAnswers();
        BigDecimal firstPart = calculate1stPart(answers);
        BigDecimal secondPart = calculate2ndPart(answers);
        BigDecimal resultScore = firstPart.add(secondPart).subtract(new BigDecimal(6)).multiply(new BigDecimal(5)).divide(new BigDecimal(19), 8, BigDecimal.ROUND_HALF_UP);
        return resultScore;
    }

    private BigDecimal calculate2ndPart(List<Integer> answers) {
        BigDecimal secondPart;
        BigDecimal secondAnswer = calculateBy2ndPartRule(answers.get(0), answers.get(1));
        BigDecimal thirdAnswer = calculateBy2ndPartRule(answers.get(1), answers.get(2));
        BigDecimal fifthAnswer = calculateBy2ndPartRule(answers.get(3), answers.get(4));
        BigDecimal sixthAnswer = calculateBy2ndPartRule(answers.get(4), answers.get(5));
        secondPart = secondAnswer.add(thirdAnswer).add(fifthAnswer).add(sixthAnswer);
        return secondPart;
    }

    private BigDecimal calculateBy2ndPartRule(Integer front, Integer current) {
        BigDecimal result = BigDecimal.ZERO;
        if (front == AnswerTypeEnum.WIN.getValue() && current == AnswerTypeEnum.SKIP.getValue()) {
            result = result.add(BigDecimal.ONE);
        } else if (front == AnswerTypeEnum.WIN.getValue() && current != AnswerTypeEnum.SKIP.getValue()) {
            result = result.add(new BigDecimal(2));
        } else if (front == AnswerTypeEnum.LOSE.getValue() && current == AnswerTypeEnum.SKIP.getValue()) {
            result = result.add(BigDecimal.ONE);
        } else if (front == AnswerTypeEnum.LOSE.getValue() && current != AnswerTypeEnum.SKIP.getValue()) {
            result = result.add(new BigDecimal(3));
        } else {
            result = BigDecimal.ZERO;
        }
        return result;
    }

    private BigDecimal calculate1stPart(List<Integer> answers) {
        BigDecimal firstPart = BigDecimal.ZERO;
        for (Integer answer : answers) {
            if (answer == AnswerTypeEnum.SKIP.getValue()) {
                firstPart = firstPart.add(BigDecimal.ONE);
            } else {
                firstPart = firstPart.add(new BigDecimal(2));
            }
        }
        return firstPart;
    }

    private BigDecimal calculateQuesPartScore(BeforeGameTestDTO beforeGameTestDTO) {
        //风测分值计算分值中年龄计算比重为0
        BigDecimal ageScore = BigDecimal.ZERO;
        HorizonEnum horizonEnum = HorizonEnum.forValue(Integer.valueOf(beforeGameTestDTO.getHorizon()));
        InvestmentBasisEnum investmentBasisEnum = InvestmentBasisEnum.forValue(Integer.valueOf(beforeGameTestDTO.getBasis()));
        InvestmentSkillEnum investmentSkillEnum = InvestmentSkillEnum.forValue(Integer.valueOf(beforeGameTestDTO.getSkill()));
        StabilityEnum stabilityEnum = StabilityEnum.forValue(Integer.valueOf(beforeGameTestDTO.getStability()));
        ToleranceEnum toleranceEnum = ToleranceEnum.forValue(Integer.valueOf(beforeGameTestDTO.getToleRance()));
        BigDecimal totalScore = ageScore.add(horizonEnum.getScore()).add(investmentBasisEnum.getScore()).add(investmentSkillEnum.getScore())
                .add(stabilityEnum.getScore()).add(toleranceEnum.getScore());
        BigDecimal resultScore = totalScore.subtract(new BigDecimal(10)).multiply(new BigDecimal(5).divide(new BigDecimal(43), 8, BigDecimal.ROUND_HALF_UP));
        return resultScore;
    }

    private RiskLevelEnum getRiskLevelByScore(BigDecimal resultScore) {
        if (resultScore.compareTo(new BigDecimal(0)) >= 0 && resultScore.compareTo(new BigDecimal(0.99)) <= 0) {
            return RiskLevelEnum.CONSERVATIVE;
        }
        if (resultScore.compareTo(new BigDecimal(1)) >= 0 && resultScore.compareTo(new BigDecimal(1.99)) <= 0) {
            return RiskLevelEnum.BALANCED;
        }
        if (resultScore.compareTo(new BigDecimal(2)) >= 0 && resultScore.compareTo(new BigDecimal(2.99)) <= 0) {
            return RiskLevelEnum.GROWTH;
        }
        if (resultScore.compareTo(new BigDecimal(3)) >= 0 && resultScore.compareTo(new BigDecimal(3.99)) <= 0) {
            return RiskLevelEnum.CONSERVATIVE;
        }
        if (resultScore.compareTo(new BigDecimal(4)) >= 0 && resultScore.compareTo(new BigDecimal(4.88)) <= 0) {
            return RiskLevelEnum.CONSERVATIVE;
        }
        return null;
    }


    private boolean checkParam(BeforeGameTestDTO beforeGameTestDTO) {
        if (StringUtils.isEmpty(beforeGameTestDTO.getAge())) {
            return false;
        }
        if (StringUtils.isEmpty(beforeGameTestDTO.getBasis())) {
            return false;
        }
        if (StringUtils.isEmpty(beforeGameTestDTO.getHorizon())) {
            return false;
        }
        if (StringUtils.isEmpty(beforeGameTestDTO.getSkill())) {
            return false;
        }
        if (StringUtils.isEmpty(beforeGameTestDTO.getStability())) {
            return false;
        }
        if (StringUtils.isEmpty(beforeGameTestDTO.getToleRance())) {
            return false;
        }
        return true;
    }

}
