package com.pivot.aham.api.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pivot.aham.api.service.client.rest.AhamRestClient;
import com.pivot.aham.api.service.client.rest.resp.ModelPortfolioDetailResp;
import com.pivot.aham.api.service.client.rest.resp.ModelPortfolioResp;
import com.pivot.aham.api.service.mapper.ModelRecommendBackUpMapper;
import com.pivot.aham.api.service.mapper.ModelRecommendMapper;
import com.pivot.aham.api.service.mapper.model.ModelRecommend;
import com.pivot.aham.api.service.mapper.model.ModelRecommendBackUp;
import com.pivot.aham.api.service.mapper.model.ProductInfoPO;
import com.pivot.aham.api.service.populator.ModelRecommendPopulator;
import com.pivot.aham.api.service.service.ModelRecommendService;
import com.pivot.aham.api.service.service.ProductInfoService;
import com.pivot.aham.api.service.service.bean.ClassfiyBean;
import com.pivot.aham.api.service.service.bean.EtfBean;
import com.pivot.aham.api.service.service.bean.EtfListBean;
import com.pivot.aham.api.service.service.bean.ModelRecommendTitleBean;
import com.pivot.aham.common.core.exception.HttpClientException;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.EmailUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.*;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.email.Email;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.support.generator.Sequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/6.
 */
@CacheConfig(cacheNames = "modelRecommend")
@Service
@Slf4j
public class ModelRecommendServiceImpl extends BaseServiceImpl<ModelRecommend, ModelRecommendMapper> implements ModelRecommendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRecommendServiceImpl.class);

    @Resource
    private ProductInfoService productInfoService;
    @Resource
    private ModelRecommendSupport modelRecommendSupport;
    @Resource
    private ModelRecommendMapper modelRecommendMapper;
    @Resource
    private ModelRecommendBackUpMapper modelRecommendBackUpMapper;


    @Override
    public List<ModelRecommend> queryByDate(Date date) {
        List<ModelRecommend> modelRecommends = mapper.queryByDate(date);
        if (CollectionUtils.isEmpty(modelRecommends)) {
            throw new BusinessException("更具该日期查询不到对应的模型数据");
        }
        return modelRecommends;
    }

    @Override
    public void synchroModelRecommend(String date) {
        List<ModelRecommend> modelRecommendList = Lists.newArrayList();
        String fileUrl = Constants.FTP_BASE_FOLDER + "/CurrWeight/";
        //1.获取所有产品信息,用于匹配
        InputStream stream = null;
        Map<String, ProductInfoPO> productInfoMap = productInfoService.getPorductInfoMap();
        if (null == productInfoMap) {
            throw new BusinessException("模型同步,没有查询到产品信息");
        }

        boolean isError = false;
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        for (PoolingEnum poolingEnum : PoolingEnum.values()) {
            String filePath = fileUrl + "CW_" + poolingEnum.getName() + "_New_" + date + ".csv";
            try {
                //需要表头进行匹配
                LOGGER.info("#####filePath:{},pooling:{},开始进行模型数据的同步", filePath, poolingEnum.getDesc());

                List<String> lines = new ArrayList();
                try {
                    stream = sftpClient.get(filePath);
                    String thisLine = "";
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    while ((thisLine = br.readLine()) != null) {
                        lines.add(thisLine);
                    }

                } finally {

                }

                if (CollectionUtils.isEmpty(lines)) {
                    throw new BusinessException(filePath + "模型文件内容为空");
                }
                //2.遍历每一列查询出对应的数据拼接modelRecommend
                List<ModelRecommend> modelRecommends = getModelRecommendList(lines, productInfoMap, poolingEnum);
                if (CollectionUtils.isNotEmpty(modelRecommends)) {
                    modelRecommendList.addAll(modelRecommends);
                }
                lines = null;
            } catch (Exception ex) {
                LOGGER.error("filePath:{}, 同步异常", filePath, ex);
                isError = true;
                throw new BusinessException(ex);
            } finally {
                try {
                    if (isError) {
                        if (stream != null) {
                            stream.close();
                        }
                        if (sftpClient != null) {
                            sftpClient.disconnect();
                        }
                    }
                } catch (IOException e) {

                }
            }
            LOGGER.info("#####filePath:{},模型数据的同步完成", fileUrl, filePath);
        }

        try {
            if (stream != null) {
                stream.close();
            }
            if (sftpClient != null) {
                sftpClient.disconnect();
            }
        } catch (Exception e) {
        }

        //3.入库,修稿之前的数据为无效。新数据生效
        if (CollectionUtils.isNotEmpty(modelRecommendList)) {
            modelRecommendSupport.saveNewModelRecommend(modelRecommendList);
        }
    }

    private List<ModelRecommend> getModelRecommendList(List<String> lines,
            Map<String, ProductInfoPO> productInfoMap,
            PoolingEnum poolingEnum) {
        Map<Integer, ModelRecommendTitleBean> modelRecommendTitleMap = getExcelTitleDetail(lines, productInfoMap);
        //遍历Excel的每行,构造ModelRecommed数据
        List<ModelRecommend> modelRecommends = Lists.newArrayList();
        for (int i = 0; i < lines.size(); i++) {
            EtfListBean etfListBean = new EtfListBean();
            List<EtfBean> mainEtfBeanList = Lists.newArrayList();
            List<EtfBean> subEtfBeanList = Lists.newArrayList();
            List<ClassfiyBean> classfiyBeanList = Lists.newArrayList();
            ModelRecommend modelRecommend = new ModelRecommend();
            String[] cols = lines.get(i).split(",");
            for (int j = 0; j <= cols.length - 1; j++) {
                ModelRecommendTitleBean titleBean = modelRecommendTitleMap.get(j);
                switch (titleBean.getExcelTitleType()) {
                    case ETF:
                        //etf(mian、sub)
                        EtfBean etfBean = new EtfBean();
                        etfBean.setEtf(titleBean.getProductInfo().getProductCode());
                        etfBean.setWeight(new BigDecimal(cols[j]));
                        if (ProductMainSubTypeEnum.MAIN == titleBean.getProductInfo().getProductType()) {
                            mainEtfBeanList.add(etfBean);
                        } else {
                            subEtfBeanList.add(etfBean);
                        }
                        break;
                    case FIRST_CLASSFIY:
                        //第一分类
                        ClassfiyBean classfiyBean = new ClassfiyBean();
                        classfiyBean.setClassfiyName(titleBean.getFirstClassfiyType());
                        classfiyBean.setPercentage(new BigDecimal(cols[j]));
                        classfiyBeanList.add(classfiyBean);
                        break;
                    case DATE:
                        //date
                        modelRecommend.setModelTime(DateUtils.parseDate(cols[j]));
                        break;
                    case SCORE:
                        //模型分
                        modelRecommend.setScore(new BigDecimal(cols[j]));
                        break;
                    case RISK:
                        //风险等级
                        modelRecommend.setRisk(RiskLevelEnum.forValue(Integer.parseInt(cols[j])));
                        break;
                    case AGE:
                        //年龄
                        modelRecommend.setAge(AgeLevelEnum.forValue(Integer.parseInt(cols[j])));
                        break;
//                    case CURRENCY: //CURRENCY 属于第一大类了
//                        modelRecommend.setCurrency(new BigDecimal(cols[j]));
//                        break;
                }
            }

            etfListBean.setMainEtf(mainEtfBeanList);
            etfListBean.setSubEtf(subEtfBeanList);
            modelRecommend.setProductWeight(JSON.toJSONString(etfListBean));
            modelRecommend.setClassfiyWeight(JSON.toJSONString(classfiyBeanList));
            modelRecommend.setModelStatus(ModelStatusEnum.Effective);
            modelRecommend.setId(Sequence.next());
            modelRecommend.setCreateTime(DateUtils.now());
            modelRecommend.setUpdateTime(DateUtils.now());
            String portfolioId = "P" + poolingEnum.getValue() + "R" + modelRecommend.getRisk().getValue() + "A" + modelRecommend.getAge().getValue();
            modelRecommend.setPortfolioId(portfolioId);
            modelRecommend.setPool(poolingEnum);
            modelRecommends.add(modelRecommend);
        }
        return modelRecommends;
    }

    /**
     * 分析Excel 的第一行,区别出哪一列是Etf,哪一列是第一分类
     *
     * @param lines
     * @param productInfoMap
     * @return
     */
    private Map<Integer, ModelRecommendTitleBean> getExcelTitleDetail(List<String> lines,
            Map<String, ProductInfoPO> productInfoMap) {
        //第一行是表头 --> 根据表头进行分类,区分出每一类是属于什么
        String[] cols = lines.get(0).split(",");
        //解析Excel的表头,key -> 列号, valye -> 列对应的产品详情
        Map<Integer, ModelRecommendTitleBean> modelRecommendTitleBeanMap = Maps.newHashMap();
        for (int i = 0; i <= cols.length - 1; i++) {
            String excelTitleValue = StringUtils.trim(cols[i]);
            FirstClassfiyTypeEnum firstClassfiyType = FirstClassfiyTypeEnum.forDesc(excelTitleValue);
            if (null != firstClassfiyType) {
                //该列属于第一类型
                ModelRecommendTitleBean titleBean = new ModelRecommendTitleBean();
                titleBean.setExcelTitleType(ExcelTitleTypeEnum.FIRST_CLASSFIY);
                titleBean.setFirstClassfiyType(firstClassfiyType);
                modelRecommendTitleBeanMap.put(i, titleBean);
                continue;
            }
            ProductInfoPO productInfo = productInfoMap.get(excelTitleValue);
            if (null != productInfo) {
                //该列属于etf -> 需要区分出事main 、sub
                ModelRecommendTitleBean titleBean = new ModelRecommendTitleBean();
                titleBean.setExcelTitleType(ExcelTitleTypeEnum.ETF);
                titleBean.setProductInfo(productInfo);
                modelRecommendTitleBeanMap.put(i, titleBean);
                continue;
            }
            ExcelTitleTypeEnum excelTitleType = ExcelTitleTypeEnum.forDesc(excelTitleValue);
            if (null != excelTitleType) {
                //Score、ageLevel、riskLevel、Currency 四类中的之一
                ModelRecommendTitleBean titleBean = new ModelRecommendTitleBean();
                titleBean.setExcelTitleType(excelTitleType);
                modelRecommendTitleBeanMap.put(i, titleBean);
                continue;
            }
            //不属于任何一种 -> 文件有误或者 基础配置表有误
            throw new BusinessException("模型excel文件中产品:" + cols[i] + "在基金基础表中没有详情");
        }
        //去掉第一列,构造剩下的模型数据
        lines.remove(0);
        return modelRecommendTitleBeanMap;
    }

    @Override
    public List<ModelRecommend> queryAllValidRecommend() {
        return mapper.listValidModel(ModelStatusEnum.Effective);
    }

    @Override
    public ModelRecommend getValidRecommendByPortfolioId(String portfolioId) {
        return mapper.getValidRecommendByPortfolioId(portfolioId);
    }

    @Override
    public void sendFtpFileNotFound(String fileNotFound) {
        try {
            String contactEmail = PropertiesUtil.getString("modelException_contactEmail");
            Email email = new Email()
                    .setTemplateName("ExceptionEmail")
                    .setTemplateVariables(InstanceUtil.newHashMap("exMsg", fileNotFound))
                    .setSendTo(contactEmail)
                    .setTopic("ftp服务器未更新数据");
            EmailUtil.sendEmail(email);
        } catch (Exception e) {
            LOGGER.error("发送读取挂载文件出错错误邮件失败:{}", ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public ModelRecommend queryModelRecommend(ModelRecommend queryParam) {
        return mapper.queryModelRecommend(queryParam);
    }

    //ftp://ftp.jimubox.com/Overseas/3001/HisWeight/
    @Override
    public void synchroHisModelRecommend(String date, int days) {
        List<ModelRecommend> modelRecommendList = Lists.newArrayList();
        String fileUrl = Constants.FTP_BASE_FOLDER + "/HisWeight/";
        InputStream stream = null;
        //1.获取所有产品信息,用于匹配
        Map<String, ProductInfoPO> productInfoMap = productInfoService.getPorductInfoMap();
        if (null == productInfoMap) {
            return;
        }

        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        try {
            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (AgeLevelEnum ageLevelEnum : AgeLevelEnum.values()) {
                    for (RiskLevelEnum riskLevelEnum : RiskLevelEnum.values()) {
                        logger.info("#####fileUrl:{},pooling:{},age:{},risk:{},开始进行历史模型数据的同步",
                                fileUrl, poolingEnum.getDesc(), ageLevelEnum.getDesc(), riskLevelEnum.getDesc());
                        String filePath = fileUrl + "HW_" + poolingEnum.getName() + "_" + riskLevelEnum.getName() + "_" + ageLevelEnum.getName() + ".csv";

                        List<String> lines = new ArrayList();
                        try {
                            stream = sftpClient.get(filePath);
                            String thisLine = "";
                            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                            while ((thisLine = br.readLine()) != null) {
                                lines.add(thisLine);
                            }

                        } finally {

                        }

                        if (CollectionUtils.isEmpty(lines)) {
                            throw new BusinessException(filePath + "模型文件内容为空");
                        }

                        //2.遍历每一列查询出对应的数据拼接modelRecommend
                        List<ModelRecommend> modelRecommends = getHisModelRecommendList(lines, productInfoMap,
                                poolingEnum, ageLevelEnum, riskLevelEnum);

                        int i = 0;
                        for (ModelRecommend modelRecommend : modelRecommends) {
                            Date beginDate = DateUtils.addDays(DateUtils.parseDate(date), i);
                            if (DateUtils.dayStart(modelRecommend.getModelTime()).compareTo(beginDate) == 0) {
                                modelRecommendList.add(modelRecommend);
                                i++;
                            }
                            if (i > days) {
                                break;
                            }
                        }
                        lines = null;
                    }
                }
            }

            modelRecommendMapper.insertBatch(Lists.newArrayList(modelRecommendList));
            LOGGER.info("#####filePath:{},模型数据的同步完成", fileUrl);
        } catch (BusinessException | IOException e) {
            LOGGER.info("#####filePath:{},Reading Historical Record failed", fileUrl);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (sftpClient != null) {
                    sftpClient.disconnect();

                }
            } catch (Exception e) {

            }
        }

    }

    @Override
    public ModelRecommendBackUp queryModelRecommendBackUp(ModelRecommendBackUp modelRecommendBackUp) {
        return modelRecommendBackUpMapper.queryModelRecommendBackUp(modelRecommendBackUp);
    }

    @Override
    public void modelRecommendInit(String date) {
        synchroModelRecommend(date);
    }

    @Override
    public ModelRecommend queryModelById(ModelRecommend queryParam) {
        return modelRecommendMapper.queryModelById(queryParam);
    }

    private List<ModelRecommend> getHisModelRecommendList(List<String> lines,
                                                          Map<String, ProductInfoPO> productInfoMap,
                                                          PoolingEnum poolingEnum,
                                                          AgeLevelEnum ageLevel,
                                                          RiskLevelEnum riskLevel) {
        Map<Integer, ModelRecommendTitleBean> modelRecommendTitleMap = getExcelTitleDetail(lines, productInfoMap);
        //遍历Excel的每行,构造ModelRecommed数据
        List<ModelRecommend> modelRecommends = Lists.newArrayList();
        for (int i = 0; i < lines.size(); i++) {
            EtfListBean etfListBean = new EtfListBean();
            List<EtfBean> mainEtfBeanList = Lists.newArrayList();
            List<EtfBean> subEtfBeanList = Lists.newArrayList();
            List<ClassfiyBean> classfiyBeanList = Lists.newArrayList();
            ModelRecommend modelRecommend = new ModelRecommend();
            String[] cols = lines.get(i).split(",");
            for (int j = 0; j < cols.length - 1; j++) {
                ModelRecommendTitleBean titleBean = modelRecommendTitleMap.get(j);
                switch (titleBean.getExcelTitleType()) {
                    case ETF:
                        //etf(mian、sub)
                        EtfBean etfBean = new EtfBean();
                        etfBean.setEtf(titleBean.getProductInfo().getProductCode());
                        etfBean.setWeight(new BigDecimal(cols[j]));
                        if (ProductMainSubTypeEnum.MAIN == titleBean.getProductInfo().getProductType()) {
                            mainEtfBeanList.add(etfBean);
                        } else {
                            subEtfBeanList.add(etfBean);
                        }
                        break;
                    case FIRST_CLASSFIY:
                        //第一分类
                        ClassfiyBean classfiyBean = new ClassfiyBean();
                        classfiyBean.setClassfiyName(titleBean.getFirstClassfiyType());
                        classfiyBean.setPercentage(new BigDecimal(cols[j]));
                        classfiyBeanList.add(classfiyBean);
                        break;
                    case DATE:
                        //date
                        modelRecommend.setModelTime(DateUtils.parseDate(cols[j]));
                        break;
                    case SCORE:
                        //模型分
                        modelRecommend.setScore(new BigDecimal(cols[j]));
                        break;
                    case RISK:
                        //风险等级
                        modelRecommend.setRisk(riskLevel);
                        break;
                    case AGE:
                        //年龄
                        modelRecommend.setAge(ageLevel);
                        break;
//                    case CURRENCY:
//                        modelRecommend.setCurrency(new BigDecimal(cols[j]));
//                        break;
                }
            }

            etfListBean.setMainEtf(mainEtfBeanList);
            etfListBean.setSubEtf(subEtfBeanList);
            modelRecommend.setProductWeight(JSON.toJSONString(etfListBean));
            modelRecommend.setClassfiyWeight(JSON.toJSONString(classfiyBeanList));
            modelRecommend.setModelStatus(ModelStatusEnum.Effective);
            modelRecommend.setId(Sequence.next());
            modelRecommend.setCreateTime(DateUtils.now());
            modelRecommend.setUpdateTime(DateUtils.now());
            modelRecommend.setAge(ageLevel);
            modelRecommend.setRisk(riskLevel);
            String portfolioId = "P" + poolingEnum.getValue() + "R" + riskLevel.getValue() + "A" + ageLevel.getValue();
            modelRecommend.setPortfolioId(portfolioId);
            modelRecommends.add(modelRecommend);
        }
        return modelRecommends;
    }

    @Override
    public void saveNewPortfolioRecommend() {
        List<ModelRecommend> modelRecommends = getNewModelRecommends();
        try {
            if (CollectionUtils.isNotEmpty(modelRecommends)){
                modelRecommendSupport.saveNewModelRecommend(modelRecommends);
            } else{
                log.info("No new model recommend");
            }
        }
        catch (Exception exception) {
            log.error("Failed to save new model portfolio", exception);
        }
    }

    private List<ModelRecommend> getNewModelRecommends(){
        List<ModelPortfolioResp> modelPortfolioResps = AhamRestClient.modelPortfolios();
        List<ModelRecommend> modelRecommends = new ArrayList<>();
        try {
            for (ModelPortfolioResp modelPortfolio : modelPortfolioResps)  {
                ModelRecommend modelRecommend = new ModelRecommend();
                if (modelPortfolio != null){
                    List<ModelPortfolioDetailResp> modelPortfolioDetailResps = AhamRestClient.getModelPortfolioDetailById(modelPortfolio.getId());
                    handleModelRecommend(modelPortfolioDetailResps, modelRecommend);
                }
                modelRecommends.add(modelRecommend);
            }
        }
        catch (HttpClientException httpClientException) {
            log.error("Calling new model portfolio failed", httpClientException);
        }
        return modelRecommends;
    }

    private void handleModelRecommend(List<ModelPortfolioDetailResp> modelPortfolioDetailResps, ModelRecommend modelRecommend){
            ModelRecommendPopulator.populateModelRecommendFromModelPortfolio(modelRecommend, modelPortfolioDetailResps);
    }

}
