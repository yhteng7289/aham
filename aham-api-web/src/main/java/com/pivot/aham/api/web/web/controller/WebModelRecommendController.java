package com.pivot.aham.api.web.web.controller;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.web.web.vo.ModelRecommendReqVo;
import com.pivot.aham.api.web.web.vo.PortLevelResVo;
import com.pivot.aham.api.web.web.vo.req.FuturePortLevelReqVo;
import com.pivot.aham.api.web.web.vo.req.ModelRecommendForAppReqVo;
import com.pivot.aham.api.web.web.vo.req.PortLevelReqVo;
import com.pivot.aham.api.web.web.vo.res.*;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.enums.RiskLevelEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

/**
 *
 * @author luyang.li
 * @date 18/11/30
 * <p>
 * 提供给FE的接口 -- 每日模型同步
 */
@RestController
@RequestMapping("/app/")
@Api(value = "每日模型同步", description = "每日模型同步")
public class WebModelRecommendController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebModelRecommendController.class);

    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;

    @PostMapping("model/recommend.api")
    @ApiOperation(value = "每日模型同步", produces = MediaType.APPLICATION_JSON_VALUE, notes
            = "每日模型同步接口需要以下1个参数：\n" + "1.用户date")
    public Message<List<ModelRecommendResVo>> modelRecommend(@RequestBody @Valid ModelRecommendReqVo modelRecommendReqVo) throws Exception {
        LOGGER.info("模型信息同步,请求参数,date:{}", JSON.toJSON(modelRecommendReqVo));
        ModelRecommendDTO modelRecommendDTO = modelRecommendReqVo.convertToDto(modelRecommendReqVo);
        RpcMessage<List<ModelRecommendResWrapper>> rpcMessage = modelServiceRemoteService.getModelRecommendDetail(modelRecommendDTO);
        List<ModelRecommendResVo> vos = null;
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            vos = BeanMapperUtils.mapList(rpcMessage.getContent(), ModelRecommendResVo.class);
        }
        LOGGER.info("模型信息同步,完成,modelRecommendResVo:{}", JSON.toJSON(vos));
        return Message.success(vos);
    }

    @PostMapping("model/recommend.app")
    @ApiOperation(value = "app策略详情", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message<ModelRecommendForAppResVo> modelRecommendForApp(@RequestBody @Valid ModelRecommendForAppReqVo modelRecommendForAppReqVo) throws Exception {
        LOGGER.info("模型信息同步,请求参数,date:{}", JSON.toJSON(modelRecommendForAppReqVo));
        ModelRecommendForAppDTO modelRecommendForAppDTO = modelRecommendForAppReqVo.convertToDto(modelRecommendForAppReqVo);
        RpcMessage<ModelRecommendForAppResWrapper> rpcMessage = modelServiceRemoteService.getModelRecommendDetailForApp(modelRecommendForAppDTO);
        ModelRecommendForAppResVo vo = new ModelRecommendForAppResVo();
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            vo.setModelData(BeanMapperUtils.mapList(rpcMessage.getContent().getModelData(), ClassfiyEtfVo.class));
            vo.setAveReturn(rpcMessage.getContent().getAveReturn());
            vo.setMaxDD(rpcMessage.getContent().getMaxDD());
            vo.setPortfolioId(rpcMessage.getContent().getPortfolioId());
            vo.setSharpRadio(rpcMessage.getContent().getSharpRadio());
            vo.setDisclaimer("* DISCLAIMER: Past, hypothetical or simulated performance is not"
                    + " necessarily indicative of future results. Unless noted otherwise, all returns"
                    + " shown herein are based on hypothetical or simulated investing. There is no"
                    + " assurance that future performance of any specific investment or investment"
                    + " recommendation by us will be profitable or suitable for you. All investments"
                    + " carry risk. You are solely responsible for accepting any investment"
                    + " recommendations and your investment decisions. We make no"
                    + " representations or warranties that any investor will, or is likely to, achieve"
                    + " profits similar to those shown.");
        }
        LOGGER.info("模型信息同步,完成,modelRecommendResVo:{}", JSON.toJSON(vo));
        return Message.success(vo);
    }

    @PostMapping("model/portLevl.api")
    @ApiOperation(value = "模型历史表现", produces = MediaType.APPLICATION_JSON_VALUE, notes = "历史表现")
    public Message<List<PortLevelResVo>> modelPortLevel(@RequestBody @Valid PortLevelReqVo portLevelReqVo) throws Exception {
        LOGGER.info("模型历史表现,完成,portLevelReqVo:{}", JSON.toJSONString(portLevelReqVo));
        PortLevelDTO portLevelDTO = portLevelReqVo.convertToDto(portLevelReqVo);
        List<PortLevelResDTO> portLevelResDTOs = modelServiceRemoteService.getPortLevel(portLevelDTO);
        List<PortLevelResVo> portLevelResVos = BeanMapperUtils.mapList(portLevelResDTOs, PortLevelResVo.class);
        LOGGER.info("模型历史表现,完成,modelRecommendResVo_size:{}", portLevelResVos.size());
        return Message.success(portLevelResVos);
    }

    @PostMapping("model/portLevel.app")
    @ApiOperation(value = "模型历史表现", produces = MediaType.APPLICATION_JSON_VALUE, notes = "历史表现")
    public Message<List<PortLevelResVo>> modelPortLevelForApp(@RequestBody @Valid PortLevelReqVo portLevelReqVo) throws Exception {
        PortLevelDTO portLevelDTO = portLevelReqVo.convertToDto(portLevelReqVo);
        List<PortLevelResDTO> portLevelResDTOs = modelServiceRemoteService.getPortLevelForApp(portLevelDTO);
        List<PortLevelResVo> portLevelResVos = BeanMapperUtils.mapList(portLevelResDTOs, PortLevelResVo.class);
        LOGGER.info("模型历史表现,完成,modelRecommendResVo_size:{}", portLevelResVos.size());
        return Message.success(portLevelResVos);
    }

    @PostMapping("model/riskLevel.app")
    @ApiOperation(value = "模型历史表现", produces = MediaType.APPLICATION_JSON_VALUE, notes = "历史表现")
    public Message<RiskLevelRemarkResVo> riskLevelDesc(@RequestBody @Valid PortLevelReqVo portLevelReqVo) throws Exception {
        RiskLevelRemarkResVo riskLevelRemarkResVo = new RiskLevelRemarkResVo();
        RiskLevelEnum riskLevelEnum = RiskLevelEnum.forValue(Integer.valueOf(portLevelReqVo.getPortfolioId().substring(3, 4)));
        riskLevelRemarkResVo.setRiskLevel(riskLevelEnum);
        riskLevelRemarkResVo.setRemark(riskLevelEnum.getRemark());
        riskLevelRemarkResVo.setValue(riskLevelEnum.getValue());
        return Message.success(riskLevelRemarkResVo);
    }

    @RequestMapping("model/portFutureLevel")
    public Message<PortFutureLevelVo> portFutureLevel(@RequestBody @Valid FuturePortLevelReqVo reqVo) throws ParseException {
        PortFutureLevelDTO dto = reqVo.convertToDto(reqVo);
        LOGGER.info("模型未来预期收益曲线,查询:{}", JSON.toJSON(reqVo));
        PortFutureLevelVo portFutureLevelVo = null;
        RpcMessage<PortFutureLevelResWrapper> rpcMessage = modelServiceRemoteService.queryPortFutureLevel(dto);
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            portFutureLevelVo = BeanMapperUtils.map(rpcMessage.getContent(), PortFutureLevelVo.class);
        }
        LOGGER.info("模型未来预期收益曲线,返回结果:{}", JSON.toJSON(portFutureLevelVo));
        return Message.success(portFutureLevelVo);
    }

    @RequestMapping("model/recommendByPortfolio")
    public Message<RecommendPortfolioResDTO> recommendByPortfolio(String portfolioId) throws ParseException {
        LOGGER.info("测试demo查询portfolioId:{}", portfolioId);
        ModelRecommendDTO dto = new ModelRecommendDTO();
        dto.setPortfolioId(portfolioId);
        RecommendPortfolioResDTO resDTO = modelServiceRemoteService.queryByPortfolio(portfolioId);
        LOGGER.info("测试demo查询返回:{}", JSON.toJSON(resDTO));
        return Message.success(resDTO);
    }

    //=========处理刷模型数据=========
    @RequestMapping("model/portLevelInit")
    public Message<Void> portLevelInit() {
        LOGGER.info("====初始化对标曲线开始====");
        modelServiceRemoteService.portLevelInit();
        LOGGER.info("====初始化对标曲线结束====");
        return Message.success();
    }

    @RequestMapping("model/modelRecommendInit")
    public Message<Void> modelRecommendInit(String date) {
        LOGGER.info("====初始化模型数据开始====");
        modelServiceRemoteService.modelRecommendInit(date);
        LOGGER.info("====初始化模型数据结束====");
        return Message.success();
    }

    @RequestMapping("model/portFutureLevelInit")
    public Message<Void> portFutureLevelInit() {
        LOGGER.info("====初始化预计收益数据开始====");
        modelServiceRemoteService.portFutureLevelInit();
        LOGGER.info("====初始化预计收益数据结束====");
        return Message.success();
    }

}
