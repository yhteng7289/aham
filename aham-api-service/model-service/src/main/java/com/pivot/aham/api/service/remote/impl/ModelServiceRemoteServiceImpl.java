package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.req.UserEtfSharesReqDTO;
import com.pivot.aham.api.server.dto.res.UserEtfSharesResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.UserStaticsRemoteService;
import com.pivot.aham.api.service.mapper.model.*;
import com.pivot.aham.api.service.service.*;
import com.pivot.aham.api.service.mapper.model.user.UserGoalInfo;
import com.pivot.aham.api.service.service.bean.EtfBean;
import com.pivot.aham.api.service.service.bean.EtfListBean;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.base.RpcMessageStandardCode;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.FirstClassfiyTypeEnum;
import com.pivot.aham.common.enums.ModelStatusEnum;
import com.pivot.aham.common.enums.PortFutureStatusEnum;
import com.pivot.aham.common.enums.ProductTradeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/6.\
 * <p>
 * 模型相关
 */
@Service(interfaceClass = ModelServiceRemoteService.class)
@Slf4j
public class ModelServiceRemoteServiceImpl implements ModelServiceRemoteService {

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    @Resource
    private ModelRecommendService modelRecommendService;

    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;

    @Resource
    private PortLevelService portLevelService;

    @Resource
    private ProductInfoService productInfoService;

    @Resource
    private PortFutureLevelService portFutureLevelService;

    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;

    @Resource
    private UserStaticsRemoteService userStaticsRemoteService;

    @Resource
    private UserGoalInfoService userGoalInfoService;

    @Override
    public List<ModelRecommendResDTO> getAllValidRecommend() {
        List<ModelRecommend> modelRecommends = modelRecommendService.queryAllValidRecommend();
        return BeanMapperUtils.mapList(modelRecommends, ModelRecommendResDTO.class);
    }

    @Override
    public ModelRecommendResDTO getValidRecommendByPortfolioId(String portfolioId) {
        ModelRecommend modelRecommend = modelRecommendService.getValidRecommendByPortfolioId(portfolioId);
        ModelRecommendResDTO modelRecommendResDTO = new ModelRecommendResDTO();
        if (modelRecommend != null) {
            BeanMapperUtils.copy(modelRecommend, modelRecommendResDTO);
        }
        PortLevel portLevelParam = new PortLevel();
        portLevelParam.setPortfolioId(portfolioId);
        String dateStr = DateUtils.formatDate(DateUtils.now(), "yyyy-MM-dd");
        portLevelParam.setModelDate(DateUtils.parseDate(dateStr));
        PortLevel portLevel = portLevelService.getPortLevel(portLevelParam);
        modelRecommendResDTO.setVooTenDays(false);
        if (portLevel != null) {
            modelRecommendResDTO.setVooTenDays(portLevel.getVooTenDays());
        }
        return modelRecommendResDTO;
    }

    @Override
    public List<ModelRecommendResDTO> getModelRecommendByDate(ModelRecommendDTO modelRecommendDTO) {
        List<ModelRecommend> modelRecommends = modelRecommendService.queryByDate(modelRecommendDTO.getModelTime());
        return BeanMapperUtils.mapList(modelRecommends, ModelRecommendResDTO.class);
    }

    @Override
    public ModelRecommendResDTO getModelRecommendById(Long modeRecommendlId) {
        ModelRecommendResDTO modelRecommendResDTO = new ModelRecommendResDTO();
        ModelRecommend modelRecommendQuery = new ModelRecommend();
        modelRecommendQuery.setId(modeRecommendlId);
        ModelRecommend modelRecommend = modelRecommendService.queryModelById(modelRecommendQuery);
        if (modelRecommend != null) {
            BeanMapperUtils.copy(modelRecommend, modelRecommendResDTO);
        }
        return modelRecommendResDTO;
    }

    @Override
    public ModelRecommendResDTO queryValidModelByPortfolioId(ModelRecommendDTO modelRecommendDTO) {
        ModelRecommend queryParam = new ModelRecommend();
        queryParam.setPortfolioId(modelRecommendDTO.getPortfolioId());
        queryParam.setModelStatus(modelRecommendDTO.getModelStatus());
        ModelRecommend modelRecommend = modelRecommendService.queryModelRecommend(queryParam);
        if (null == modelRecommend) {
            return null;
        }
        return BeanMapperUtils.map(modelRecommend, ModelRecommendResDTO.class);
    }

    @Override
    public List<ProductInfoResDTO> queryAllProductInfo() {
        ProductInfoPO productInfo = new ProductInfoPO();
        productInfo.setProductStatus(ProductTradeStatusEnum.TRADE);
        List<ProductInfoPO> productInfoList = productInfoService.listProductInfo(productInfo);
        if (CollectionUtils.isEmpty(productInfoList)) {
            return null;
        }
        return BeanMapperUtils.mapList(productInfoList, ProductInfoResDTO.class);
    }

    @Override
    public void triggerRecommendJob(String date) {
        modelRecommendService.synchroModelRecommend(date);
    }

    @Override
    public void triggerPortlevel() {
        portLevelService.synchroPortLevel();
    }

    @Override
    public RecommendPortfolioResDTO queryByPortfolio(String portfolioId) throws ParseException {
        ModelRecommendBackUp queryParam = new ModelRecommendBackUp();
        queryParam.setPortfolioId(portfolioId);
        queryParam.setModelStatus(ModelStatusEnum.Effective);
        ModelRecommendBackUp modelRecommend = modelRecommendService.queryModelRecommendBackUp(queryParam);
        EtfListBean etfListBean = JSON.parseObject(modelRecommend.getProductWeight(), new TypeReference<EtfListBean>() {
        });

        ProductInfoPO productInfoParam = new ProductInfoPO();
        productInfoParam.setProductStatus(ProductTradeStatusEnum.TRADE);
        List<ProductInfoPO> productInfoPOs = productInfoService.listProductInfo(productInfoParam);
        Map<String, ProductInfoPO> classfiyTypeEnumMap = productInfoPOs.stream().collect(Collectors.toMap(ProductInfoPO::getProductCode, item -> item));

        RecommendPortfolioResDTO resDTO = new RecommendPortfolioResDTO();
        List<ProductWeight> fixedIncome = Lists.newArrayList();
        List<ProductWeight> alternative = Lists.newArrayList();
        List<ProductWeight> developedEquity = Lists.newArrayList();
        List<ProductWeight> emergingEquity = Lists.newArrayList();
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (EtfBean etfBean : etfListBean.getMainEtf()) {
            ProductInfoPO productInfoPO = classfiyTypeEnumMap.get(etfBean.getEtf());

            ProductWeight productWeight = new ProductWeight();
            productWeight.setEtf(etfBean.getEtf());
            productWeight.setWeight(etfBean.getWeight().setScale(4, BigDecimal.ROUND_DOWN));
            productWeight.setUrl(productInfoPO.getUrl());
            switch (productInfoPO.getFirstClassfiyType()) {
                case FIXED_INCOME:
                    fixedIncome.add(productWeight);
                    break;
                case ALTERNATIVE:
                    alternative.add(productWeight);
                    break;
                case DEVELOPED_EQUITY:
                    developedEquity.add(productWeight);
                    break;
                case EMERGING_EQUITY:
                    emergingEquity.add(productWeight);
                    break;
                case CASH2:
//                    cash.add(productWeight);
                    break;
            }

            if (productInfoPO.getFirstClassfiyType() != FirstClassfiyTypeEnum.CASH2) {
                totalWeight = totalWeight.add(productWeight.getWeight());
            }
        }

        RecommendPortfolio alternativeRecommend = new RecommendPortfolio();
        alternativeRecommend.setProductWeights(alternative);
        alternativeRecommend.setClassfiyName("Alternative");
        resDTO.setAlternative(alternativeRecommend);

        RecommendPortfolio fixedIncomeRecommend = new RecommendPortfolio();
        fixedIncomeRecommend.setProductWeights(fixedIncome);
        fixedIncomeRecommend.setClassfiyName("Fixed Income");
        resDTO.setFixedIncome(fixedIncomeRecommend);

        RecommendPortfolio developedEquityRecommend = new RecommendPortfolio();
        developedEquityRecommend.setProductWeights(developedEquity);
        developedEquityRecommend.setClassfiyName("Developed Equity");
        resDTO.setDevelopedEquity(developedEquityRecommend);

        RecommendPortfolio emergingEquityRecommend = new RecommendPortfolio();
        emergingEquityRecommend.setProductWeights(emergingEquity);
        emergingEquityRecommend.setClassfiyName("Emerging Equity");
        resDTO.setEmergingEquity(emergingEquityRecommend);

        ProductWeight cashProductWeight = new ProductWeight();
        cashProductWeight.setEtf("CASH");
        cashProductWeight.setWeight(BigDecimal.ONE.subtract(totalWeight).setScale(4, BigDecimal.ROUND_DOWN));
        RecommendPortfolio cashEquityRecommend = new RecommendPortfolio();
        cashEquityRecommend.setProductWeights(Lists.newArrayList(cashProductWeight));
        cashEquityRecommend.setClassfiyName("Cash");
        resDTO.setCash(cashEquityRecommend);

        PortLevel portLevelParam = new PortLevel();
        portLevelParam.setPortfolioId(portfolioId);
        Date date = DateUtils.addDays(DateUtils.now(), -1);
        portLevelParam.setModelDate(DateUtils.parseDate(DateUtils.formatDate(date), DateUtils.DATE_FORMAT));
        PortLevel portLevel = portLevelService.getPortLevel(portLevelParam);
        if (null == portLevel) {
            portLevel = portLevelService.getLastPortLevel(portfolioId);
        }
        resDTO.setReturnVol(portLevel.getReturnVol().setScale(4, BigDecimal.ROUND_HALF_UP));
        return resDTO;
    }

    @Override
    public void triggerHisRecommendJob(String date, int days) {
        modelRecommendService.synchroHisModelRecommend(date, days);
    }

    @Override
    public RpcMessage<PortFutureLevelResWrapper> queryPortFutureLevel(PortFutureLevelDTO dto) {
        //模型预期收益
        PortFutureLevelPO queryParam = new PortFutureLevelPO();
        queryParam.setStatus(PortFutureStatusEnum.Effective);
        queryParam.setPortfolioId(dto.getPortfolioId());
        List<PortFutureLevelPO> portFutureLevels = portFutureLevelService.queryPortFutureLevel(queryParam);
        List<PortFutureLevelResDTO> portFutureLevelResDTOs = portFutureLevels.stream().map(item -> {
            PortFutureLevelResDTO resDTO = new PortFutureLevelResDTO();
            resDTO.setDate(item.getModelDate());
            resDTO.setNinetyFiveLow(item.getNinetyFiveLow().setScale(6, BigDecimal.ROUND_HALF_UP));
            resDTO.setNinetyFiveUp(item.getNinetyFiveUp().setScale(6, BigDecimal.ROUND_HALF_UP));
            resDTO.setPortfolioId(item.getPortfolioId());
            resDTO.setRcmd(item.getRcmd().setScale(6, BigDecimal.ROUND_HALF_UP));
            resDTO.setSixtyEightLow(item.getSixtyEightLow().setScale(6, BigDecimal.ROUND_HALF_UP));
            resDTO.setSixtyEightUp(item.getSixtyEightUp().setScale(6, BigDecimal.ROUND_HALF_UP));
            return resDTO;
        }).collect(Collectors.toList());

        List<ClassfiyEtfWrapper> classfiyEtfWrappers = Lists.newArrayList();
        if (checkHasGoal(dto)) {
            classfiyEtfWrappers = getClassfiyEtfWrapperByGoal(dto.getGoalId());

        }

        if (classfiyEtfWrappers == null) {
            //该goalId下的持有配置
            ModelRecommend modelRecommendParam = new ModelRecommend();
            modelRecommendParam.setPortfolioId(dto.getPortfolioId());
            modelRecommendParam.setModelStatus(ModelStatusEnum.Effective);
            ModelRecommend modelRecommend = modelRecommendService.queryModelRecommend(modelRecommendParam);
            EtfListBean etfListBean = JSON.parseObject(modelRecommend.getProductWeight(), new TypeReference<EtfListBean>() {
            });
            classfiyEtfWrappers = getClassfiyEtfWrapper(etfListBean.getMainEtf());
        }

        PortFutureLevelResWrapper futureLevelResWrapper = new PortFutureLevelResWrapper();
        futureLevelResWrapper.setFuturePortLevelDetails(portFutureLevelResDTOs);
        futureLevelResWrapper.setModelData(classfiyEtfWrappers);
        return RpcMessage.success(futureLevelResWrapper);
    }

    private boolean checkHasGoal(PortFutureLevelDTO dto) {
        UserGoalInfo queryPo = new UserGoalInfo();
        queryPo.setGoalId(dto.getGoalId()).setPortfolioId(dto.getPortfolioId());
        UserGoalInfo userGoalInfoPO = userGoalInfoService.queryUserGoalInfo(queryPo);
        if (userGoalInfoPO != null) {
            return true;
        } else {
            return false;
        }
    }

    private List<ClassfiyEtfWrapper> getClassfiyEtfWrapperByGoal(String goalId) {
        List<ProductInfoResDTO> productInfoResDTOList = modelServiceRemoteService.queryAllProductInfo();
        Map<String, ProductInfoResDTO> mapProduct = productInfoResDTOList
                .stream().collect(Collectors.toMap(ProductInfoResDTO::getProductCode, item -> item));
        UserEtfSharesReqDTO userEtfSharesReqDTO = new UserEtfSharesReqDTO();
        userEtfSharesReqDTO.setGoalId(goalId);
        userEtfSharesReqDTO.setStaticDate(getCalDate());
        RpcMessage<List<UserEtfSharesResDTO>> rpcMessage_userEtf = userStaticsRemoteService.getUserEtfShares(userEtfSharesReqDTO);
        Map<FirstClassfiyTypeEnum, List<ProductWeight>> classfiyTypeEnumListMap = Maps.newHashMap();
        if (rpcMessage_userEtf.isSuccess()) {
            List<UserEtfSharesResDTO> userEtfSharesResDTOList = rpcMessage_userEtf.getContent();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (UserEtfSharesResDTO clientGoalEtfRes : userEtfSharesResDTOList) {
                totalAmount = totalAmount.add(clientGoalEtfRes.getMoney());
            }

            // If has goal but asset is 0 means no money at all;
            if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
                return null;
            }

            BigDecimal totalPrecent = BigDecimal.ZERO;

            for (int i = 0; i < userEtfSharesResDTOList.size(); i++) {
                UserEtfSharesResDTO userEtfSharesResDTO = userEtfSharesResDTOList.get(i);
                ProductInfoResDTO productInfoResDTO = mapProduct.get(userEtfSharesResDTO.getProductCode().toUpperCase());
                List<ProductWeight> productWeights = classfiyTypeEnumListMap.get(productInfoResDTO.getFirstClassfiyType());
                if (null == productWeights) {
                    productWeights = Lists.newArrayList();
                    classfiyTypeEnumListMap.put(productInfoResDTO.getFirstClassfiyType(), productWeights);
                }
                ProductWeight productWeight = new ProductWeight();
                //如果是最后一个
                if (i == userEtfSharesResDTOList.size() - 1) {
                    BigDecimal precent = new BigDecimal("1").subtract(totalPrecent);
                    productWeight.setWeight(precent);
                    productWeight.setEtf(productInfoResDTO.getProductCode());
                    productWeight.setUrl(productInfoResDTO.getUrl());
                } else {
                    BigDecimal precent = userEtfSharesResDTO.getMoney().divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP);
                    totalPrecent = totalPrecent.add(precent);
                    productWeight.setWeight(precent);
                    productWeight.setEtf(productInfoResDTO.getProductCode());
                    productWeight.setUrl(productInfoResDTO.getUrl());
                }
                productWeights.add(productWeight);
            }
        }
        List<ClassfiyEtfWrapper> classfiyBeanList = Lists.newArrayList();
        for (FirstClassfiyTypeEnum firstClassfiyTypeEnum : FirstClassfiyTypeEnum.values()) {
            List<ProductWeight> etfPercentageVos = classfiyTypeEnumListMap.get(firstClassfiyTypeEnum);
            BigDecimal percent = BigDecimal.ZERO;
            if (null == etfPercentageVos) {
                etfPercentageVos = Lists.newArrayList();
            }
            for (ProductWeight etfPercentageVo : etfPercentageVos) {
                percent = percent.add(etfPercentageVo.getWeight());
            }
            ClassfiyEtfWrapper classfiyEtfWrapper = new ClassfiyEtfWrapper();
            classfiyEtfWrapper.setClassfiyName(firstClassfiyTypeEnum.getDesc());
            classfiyEtfWrapper.setPercentage(percent);
            classfiyEtfWrapper.setEtfPercentageVoList(etfPercentageVos);
            classfiyBeanList.add(classfiyEtfWrapper);
        }
        return classfiyBeanList;
    }

    private Date getCalDate() {
        //当前时间大于16：30，返回当天，否则返回昨日
        Date now = DateUtils.now();
        Date tDate = DateUtils.getDate(new Date(), 16, 30, 0);
        if (now.compareTo(tDate) > 0) {
            return now;
        } else {
            return DateUtils.addDateByDay(now, -1);
        }
    }

    private List<ClassfiyEtfWrapper> getClassfiyEtfWrapper(List<EtfBean> mainEtf) {
        Map<String, ProductInfoResDTO> classfiyTypeEnumMap = queryAllProductInfo().stream()
                .collect(Collectors.toMap(ProductInfoResDTO::getProductCode, item -> item));
        Map<FirstClassfiyTypeEnum, List<ProductWeight>> classfiyTypeEnumListMap = Maps.newHashMap();
        for (EtfBean etfBean : mainEtf) {
            ProductInfoResDTO productInfoResDTO = classfiyTypeEnumMap.get(etfBean.getEtf());
            List<ProductWeight> etfPercentages = classfiyTypeEnumListMap.get(productInfoResDTO.getFirstClassfiyType());
            if (null == etfPercentages) {
                etfPercentages = Lists.newArrayList();
                classfiyTypeEnumListMap.put(productInfoResDTO.getFirstClassfiyType(), etfPercentages);
            }
            ProductWeight productWeight = new ProductWeight();
            productWeight.setEtf(etfBean.getEtf());
            productWeight.setWeight(etfBean.getWeight());
            productWeight.setUrl(productInfoResDTO.getUrl());
            etfPercentages.add(productWeight);
        }
        List<ClassfiyEtfWrapper> classfiyBeanList = Lists.newArrayList();
        for (FirstClassfiyTypeEnum firstClassfiyTypeEnum : FirstClassfiyTypeEnum.values()) {
            List<ProductWeight> etfPercentageVos = classfiyTypeEnumListMap.get(firstClassfiyTypeEnum);
            BigDecimal percent = BigDecimal.ZERO;
            if (null == etfPercentageVos) {
                etfPercentageVos = Lists.newArrayList();
            }
            for (ProductWeight etfPercentageVo : etfPercentageVos) {
                percent = percent.add(etfPercentageVo.getWeight());
            }
            ClassfiyEtfWrapper classfiyEtfWrapper = new ClassfiyEtfWrapper();
            classfiyEtfWrapper.setClassfiyName(firstClassfiyTypeEnum.getDesc());
            classfiyEtfWrapper.setPercentage(percent);
            classfiyEtfWrapper.setEtfPercentageVoList(etfPercentageVos);
            classfiyBeanList.add(classfiyEtfWrapper);
        }

        return classfiyBeanList;
    }

    @Override
    public void portLevelInit() {
        portLevelService.portLevelInit();
    }

    @Override
    public void modelRecommendInit(String date) {
        modelRecommendService.modelRecommendInit(date);
    }

    @Override
    public void portFutureLevelInit() {
        portFutureLevelService.synchroPortFutureLevel(DateUtils.now());
    }

    @Override
    public RpcMessage<List<ModelRecommendResWrapper>> getModelRecommendDetail(ModelRecommendDTO modelRecommendDTO) {
        List<ModelRecommendResWrapper> modelRecommendResWrappers = Lists.newArrayList();
        List<ModelRecommendResDTO> modelRecommendResDTOs = getAllValidRecommend();

        for (ModelRecommendResDTO dto : modelRecommendResDTOs) {
            PortLevel portLevelParam = new PortLevel();
            portLevelParam.setPortfolioId(dto.getPortfolioId());
            portLevelParam.setModelDate(dto.getModelTime());
            PortLevel portLevel = null;
            portLevel = portLevelService.getPortLevel(portLevelParam);
            if (null == portLevel) {
                portLevel = portLevelService.getLastPortLevel(dto.getPortfolioId());
            }
            EtfListBean etfListBeans = JSON.parseObject(dto.getProductWeight(), new TypeReference<EtfListBean>() {
            });
            List<ClassfiyEtfWrapper> classfiyEtfWrappers = getClassfiyEtfWrapper(etfListBeans.getMainEtf());
            ModelRecommendResWrapper resWrapper = new ModelRecommendResWrapper();
            resWrapper.setScore(dto.getScore());
            resWrapper.setPortfolioId(dto.getPortfolioId());
            resWrapper.setDate(DateUtils.formatDate(modelRecommendDTO.getModelTime()));
            resWrapper.setModelData(classfiyEtfWrappers);
            resWrapper.setPortfolioAveReturn(portLevel.getReturnVol());
            modelRecommendResWrappers.add(resWrapper);
        }
        return RpcMessage.success(modelRecommendResWrappers);
    }

    @Override
    public RpcMessage<ModelRecommendForAppResWrapper> getModelRecommendDetailForApp(ModelRecommendForAppDTO modelRecommendForAppDTO) {
        ModelRecommendForAppResWrapper modelRecommendForAppResWrapper = new ModelRecommendForAppResWrapper();
        modelRecommendForAppResWrapper.setPortfolioId(modelRecommendForAppDTO.getPortfolioId());
        ModelRecommend modelRecommend = modelRecommendService.getValidRecommendByPortfolioId(modelRecommendForAppDTO.getPortfolioId());
        buildModelData(modelRecommendForAppResWrapper, modelRecommend);
        //误差修正
        BigDecimal totalPercent = BigDecimal.ZERO;
        BigDecimal errorPercent = BigDecimal.ZERO;
        BigDecimal maxPercent = BigDecimal.ZERO;
        String maxClassfiyName = null;
        for (ClassfiyEtfWrapper classfiyEtfWrapper : modelRecommendForAppResWrapper.getModelData()) {
            if (classfiyEtfWrapper.getPercentage().compareTo(maxPercent) > 0) {
                maxPercent = classfiyEtfWrapper.getPercentage();
                maxClassfiyName = classfiyEtfWrapper.getClassfiyName();
            }
            totalPercent = totalPercent.add(classfiyEtfWrapper.getPercentage());
        }
        if (totalPercent.compareTo(HUNDRED) != 0) {
            errorPercent = HUNDRED.subtract(totalPercent);
        }
        if (errorPercent.compareTo(BigDecimal.ZERO) != 0) {
            repairError(modelRecommendForAppResWrapper.getModelData(), maxClassfiyName, errorPercent);
        }
        return RpcMessage.success(modelRecommendForAppResWrapper);
    }

    private void repairError(List<ClassfiyEtfWrapper> modelData, String maxClassfiyName, BigDecimal errorPercent) {
        for (ClassfiyEtfWrapper classfiyEtfWrapper : modelData) {
            if (classfiyEtfWrapper.getClassfiyName().equals(maxClassfiyName)) {
                classfiyEtfWrapper.setPercentage(classfiyEtfWrapper.getPercentage().add(errorPercent));
            }
        }
    }

    @Override
    public List<PortLevelResDTO> getPortLevel(PortLevelDTO portLevelDTO) {
        List<PortLevel> portLevels = portLevelService.getPortLevels(portLevelDTO);
        if (CollectionUtils.isEmpty(portLevels)) {
            log.error("###########查询收益曲线没有数据");
            return Lists.newArrayList();
        }
        PortLevel firstPortLevel = portLevelService.getFirstPortLevel(portLevelDTO.getPortfolioId());

        List<PortLevelResDTO> portLevelResDTOs = Lists.newArrayList();
        for (PortLevel portLevel : portLevels) {
            PortLevelResDTO portLevelResDTO = new PortLevelResDTO();
            portLevelResDTO.setDate(portLevel.getModelDate());
            portLevelResDTO.setBenchmarkData(portLevel.getBenchmarkData().divide(firstPortLevel.getBenchmarkData(), 2, BigDecimal.ROUND_HALF_UP));
            portLevelResDTO.setPortfolioLevel(portLevel.getPortfolioLevel().divide(firstPortLevel.getPortfolioLevel(), 2, BigDecimal.ROUND_HALF_UP));
            portLevelResDTOs.add(portLevelResDTO);
        }
        return portLevelResDTOs;
    }

    @Override
    public List<PortLevelResDTO> getPortLevelForApp(PortLevelDTO portLevelDTO) {
        List<PortLevel> portLevels = portLevelService.getPortLevels(portLevelDTO);
        if (CollectionUtils.isEmpty(portLevels)) {
            log.error("###########查询收益曲线没有数据");
            return Lists.newArrayList();
        }
        PortLevel firstPortLevel = portLevelService.getFirstPortLevel(portLevelDTO.getPortfolioId());

        List<PortLevelResDTO> portLevelResDTOs = Lists.newArrayList();
        for (PortLevel portLevel : portLevels) {
            PortLevelResDTO portLevelResDTO = new PortLevelResDTO();
            portLevelResDTO.setDate(DateUtils.parseDate(DateUtils.formatDate(portLevel.getModelDate(), DateUtils.DATE_FORMAT)));
            portLevelResDTO.setBenchmarkData(portLevel.getBenchmarkData().divide(firstPortLevel.getBenchmarkData(), 2, BigDecimal.ROUND_HALF_UP));
            portLevelResDTO.setPortfolioLevel(portLevel.getPortfolioLevel().divide(firstPortLevel.getPortfolioLevel(), 2, BigDecimal.ROUND_HALF_UP));
            // Logic removed as Johnny requested
//            if (DateUtils.getDay(portLevel.getModelDate()).equals("01")) {
//                portLevelResDTOs.add(portLevelResDTO);
//            }

            portLevelResDTOs.add(portLevelResDTO);
        }
        return portLevelResDTOs;
    }

    @Override
    public void triggerAhamRecommendJob() {
        modelRecommendService.saveNewPortfolioRecommend();
    }

    private void buildModelData(ModelRecommendForAppResWrapper modelRecommendForAppResWrapper, ModelRecommend modelRecommend) {
        PortLevel portLevelParam = new PortLevel();
        portLevelParam.setPortfolioId(modelRecommend.getPortfolioId());
        portLevelParam.setModelDate(modelRecommend.getModelTime());
        PortLevel portLevel = null;
        portLevel = portLevelService.getPortLevel(portLevelParam);
        if (null == portLevel) {
            portLevel = portLevelService.getLastPortLevel(modelRecommend.getPortfolioId());
        }
        EtfListBean etfListBeans = JSON.parseObject(modelRecommend.getProductWeight(), new TypeReference<EtfListBean>() {
        });
        List<ClassfiyEtfWrapper> classfiyEtfWrappers = getClassfiyEtfWrapperforApp(etfListBeans.getMainEtf());
        modelRecommendForAppResWrapper.setAveReturn(String.valueOf(portLevel.getReturnVol().setScale(2, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED) + "% p.a. *"));
        modelRecommendForAppResWrapper.setModelData(classfiyEtfWrappers);
        modelRecommendForAppResWrapper.setMaxDD(String.valueOf(portLevel.getMaxDD().setScale(2, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED) + "%"));
//        modelRecommendForAppResWrapper.setMaxDD(String.valueOf(portLevel.getMaxDD().setScale(2, BigDecimal.ROUND_HALF_UP)));
//        modelRecommendForAppResWrapper.setSharpRadio(String.valueOf(portLevel.getReturnVol().divide(portLevel.getVol(), 2, BigDecimal.ROUND_HALF_UP).multiply(HUNDRED) + "%"));
        modelRecommendForAppResWrapper.setSharpRadio(String.valueOf(portLevel.getReturnVol().divide(portLevel.getVol(), 2, BigDecimal.ROUND_HALF_UP)));
    }

    private List<ClassfiyEtfWrapper> getClassfiyEtfWrapperforApp(List<EtfBean> mainEtf) {
        Map<String, ProductInfoResDTO> classfiyTypeEnumMap = queryAllProductInfo().stream()
                .collect(Collectors.toMap(ProductInfoResDTO::getProductCode, item -> item));
        Map<FirstClassfiyTypeEnum, List<ProductWeight>> classfiyTypeEnumListMap = Maps.newHashMap();
        for (EtfBean etfBean : mainEtf) {
            ProductInfoResDTO productInfoResDTO = classfiyTypeEnumMap.get(etfBean.getEtf());
            List<ProductWeight> etfPercentages = classfiyTypeEnumListMap.get(productInfoResDTO.getFirstClassfiyType());
            if (null == etfPercentages) {
                etfPercentages = Lists.newArrayList();
                classfiyTypeEnumListMap.put(productInfoResDTO.getFirstClassfiyType(), etfPercentages);
            }
            ProductWeight productWeight = new ProductWeight();
            productWeight.setEtf(etfBean.getEtf());
            productWeight.setWeight(etfBean.getWeight());
            productWeight.setUrl(productInfoResDTO.getUrl());
            etfPercentages.add(productWeight);
        }
        List<ClassfiyEtfWrapper> classfiyBeanList = Lists.newArrayList();
        for (FirstClassfiyTypeEnum firstClassfiyTypeEnum : FirstClassfiyTypeEnum.values()) {
            List<ProductWeight> etfPercentageVos = classfiyTypeEnumListMap.get(firstClassfiyTypeEnum);
            BigDecimal percent = BigDecimal.ZERO;
            if (null == etfPercentageVos) {
                etfPercentageVos = Lists.newArrayList();
            }
            for (ProductWeight etfPercentageVo : etfPercentageVos) {
                percent = percent.add(etfPercentageVo.getWeight());
            }
            ClassfiyEtfWrapper classfiyEtfWrapper = new ClassfiyEtfWrapper();
            classfiyEtfWrapper.setClassfiyName(firstClassfiyTypeEnum.getDesc());
            classfiyEtfWrapper.setPercentage(percent.multiply(HUNDRED).setScale(0, BigDecimal.ROUND_HALF_DOWN));
            classfiyEtfWrapper.setEtfPercentageVoList(etfPercentageVos);
            classfiyBeanList.add(classfiyEtfWrapper);
        }
        return classfiyBeanList;
    }

    /**
     * 统计每个portfolio下的所有投资的平均收益
     *
     * @param portfolioId
     * @return
     */
    private BigDecimal getPortfolioAveReturn(String portfolioId) {
        log.info("portfolioId:{},查询收益开始", portfolioId);
        RpcMessage<List<UserProfitInfoResDTO>> rpcMessage = assetServiceRemoteService.queryPortfolioReturn(portfolioId);
        log.info("portfolioId:{},查询收益返回,userProfitInfoResDTOs:{}", portfolioId, JSON.toJSON(rpcMessage));
        BigDecimal aveReturn = BigDecimal.ZERO;
        if (RpcMessageStandardCode.OK.value() == rpcMessage.getResultCode()) {
            BigDecimal totalProfit = BigDecimal.ZERO;
            List<UserProfitInfoResDTO> userProfitInfoResDTOs = rpcMessage.getContent();
            if (CollectionUtils.isEmpty(userProfitInfoResDTOs)) {
                return BigDecimal.ZERO;
            }
            for (UserProfitInfoResDTO userProfitInfoResDTO : userProfitInfoResDTOs) {
                totalProfit = totalProfit.add(userProfitInfoResDTO.getTotalProfit());
            }
            aveReturn = totalProfit.divide(new BigDecimal(userProfitInfoResDTOs.size()), 4, BigDecimal.ROUND_HALF_UP);
        }
        log.info("portfolioId:{},查询收益返回,aveReturn:{}", portfolioId, aveReturn);
        return aveReturn;
    }

}
