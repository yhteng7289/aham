package com.pivot.aham.api.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Splitter;
import com.pivot.aham.api.server.dto.PortLevelDTO;
import com.pivot.aham.api.service.mapper.PortLevelMapper;
import com.pivot.aham.api.service.mapper.model.PortLevel;
import com.pivot.aham.api.service.service.ModelRecommendService;
import com.pivot.aham.api.service.service.PortLevelService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.PoolingEnum;
import com.pivot.aham.common.enums.RebalanceEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import java.io.BufferedReader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by luyang.li on 18/12/7.
 */
@CacheConfig(cacheNames = "portLevel")
@Service
public class PortLevelServiceImpl extends BaseServiceImpl<PortLevel, PortLevelMapper> implements PortLevelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortLevelServiceImpl.class);

    @Autowired
    private PortLevelMapper portLevelMapper;
    @Resource
    private ModelRecommendService modelRecommendService;

    private static final Splitter SPLITTER = Splitter.on(".").trimResults();

    @Override
    public List<PortLevel> getPortLevels(PortLevelDTO portLevelDTO) {
        PortLevel portLevel = new PortLevel();
        portLevel.setPortfolioId(portLevelDTO.getPortfolioId());
        return portLevelMapper.getPortLevels(portLevel);
    }

    @Override
    public void synchroPortLevel() {
        String fileUrl = Constants.FTP_BASE_FOLDER + "/PORTLEVEL/";
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        List<String> errorFiles = Lists.newArrayList();
        InputStream stream = null;
        try {
            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
                    for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
                        LOGGER.info("#####pooling:{},riskLevel:{},ageLevel:{},开始进行走势图的同步",
                                poolingEnum.getValue(), riskEnum.getValue(), ageEnum.getValue());
                        String fileName = poolingEnum.getName() + "_NAV_Tracking_" + riskEnum.getName() + "_" + ageEnum.getName() + ".csv";
                        String portfolioId = "P" + poolingEnum.getValue() + "R" + riskEnum.getValue() + "A" + ageEnum.getValue();
                        try {
                            Date now = DateUtils.now();
                            Date yesterDay = DateUtils.dayStart(DateUtils.addDays(now, -1));
                            String filePath = fileUrl + fileName;
//                            filePath = PropertiesUtil.getString("ftp.jimubox") + File.separator + filePath;
                            LOGGER.info("####PortLevel_filePath:{}", filePath);
//                            List<String> lines = FTPClientUtil.readFileContent(filePath);
                            List<String> lines = new ArrayList();
                            try {
                                stream = sftpClient.get(filePath);
                                String thisLine = "";
                                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                                while ((thisLine = br.readLine()) != null) {
                                    lines.add(thisLine);
                                }

                            } catch (Exception e) {

                            }
                            
                            LOGGER.info("lines : " + lines.size());
                            //获取最新的记录
                            String[] cols = lines.get(lines.size() - 1).split(",");
                            Date lastModelDate = DateUtils.parseDate(cols[0], DateUtils.DATE_FORMAT2);
                            if (yesterDay.compareTo(lastModelDate) != 0) {
                                String fileNotFound = "请检查ftp服务器文件:" + filePath + ",中是否更新数据";
                                LOGGER.error(fileNotFound);
                                throw new BusinessException(fileNotFound);
                            }
                            PortLevel portLevel = new PortLevel();
                            Boolean vooTenDays = false;
                            if (cols[7].equals("1")) {
                                vooTenDays = true;
                            }
                            portLevel.setModelDate(lastModelDate)
                                    .setPortfolioLevel(new BigDecimal(cols[1]))
                                    .setMaxDD(new BigDecimal(cols[2]))
                                    .setVol(new BigDecimal(cols[3]))
                                    .setReturnVol(new BigDecimal(cols[4]))
                                    .setRebalance(RebalanceEnum.forValue(Integer.parseInt(cols[5])))
                                    .setBenchmarkData(new BigDecimal(cols[6]))
                                    .setVooTenDays(vooTenDays)
                                    .setPortfolioId(portfolioId)
                                    .setCreateTime(now)
                                    .setUpdateTime(now)
                                    .setId(Sequence.next());
                            PortLevel queryParam = new PortLevel();
                            queryParam.setPortfolioId(portLevel.getPortfolioId());
                            queryParam.setModelDate(portLevel.getModelDate());
                            PortLevel oldPortLevel = portLevelMapper.getPortLevel(queryParam);
                            if (null == oldPortLevel) {
                                portLevelMapper.insertBatch(Lists.newArrayList(portLevel));
                            } else {
                                portLevel.setId(oldPortLevel.getId());
                                portLevelMapper.updatePortLevel(portLevel);
                            }
                            lines = null;
                        } catch (Exception ex) {
                            LOGGER.error("fileUrl:{},fileName:{}, 同步走势图异常", fileUrl, fileName, ex);
                            errorFiles.add(ex.getMessage());
//                            modelRecommendService.sendFtpFileNotFound(ExceptionUtils.getMessage(ex));
                        }
                        LOGGER.info("#####fileUrl:{},fileName:{},进行走势图的同步完成", fileUrl, fileName);

                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("#####同步用户收益取消异常:", ex);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (sftpClient != null) {
                    sftpClient.disconnect();
                }
            } catch (IOException e) {

            }
        }
        if (CollectionUtils.isNotEmpty(errorFiles)) {
            ErrorLogAndMailUtil.logError(LOGGER, errorFiles);
        }

    }

    @Override
    public PortLevel getPortLevel(PortLevel portLevelParam) {
        return portLevelMapper.getPortLevel(portLevelParam);
    }

    @Override
    public PortLevel getLastPortLevel(String portfolioId) {
        return portLevelMapper.getLastPortLevel(portfolioId);
    }

    @Override
    public void portLevelInit() {
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        String fileUrl = Constants.FTP_BASE_FOLDER + "/PORTLEVEL/";
        InputStream stream = null;
        try {
            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
                    for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
                        LOGGER.info("#####pooling:{},riskLevel:{},ageLevel:{},开始进行走势图的init",
                                poolingEnum.getDesc(), riskEnum.getDesc(), ageEnum.getDesc());
                        String fileName = poolingEnum.getName() + "_NAV_Tracking_" + riskEnum.getName() + "_" + ageEnum.getName() + ".csv";
                        String portfolioId = "P" + poolingEnum.getValue() + "R" + riskEnum.getValue() + "A" + ageEnum.getValue();
                        try {
                            Date now = DateUtils.now();
                            String filePath = fileUrl + fileName;

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
                                continue;
                            }
                            List<PortLevel> portLevels = Lists.newArrayList();
                            for (String line : lines) {
                                String[] cols = line.split(",");
                                Date lastModelDate = DateUtils.parseDate(cols[0], DateUtils.DATE_FORMAT2);

                                PortLevel portLevel = new PortLevel();
                                Boolean vooTenDays = false;
                                if (cols[7].equals("1")) {
                                    vooTenDays = true;
                                }
                                portLevel.setModelDate(lastModelDate)
                                        .setPortfolioLevel(new BigDecimal(cols[1]))
                                        .setMaxDD(new BigDecimal(cols[2]))
                                        .setVol(new BigDecimal(cols[3]))
                                        .setReturnVol(new BigDecimal(cols[4]))
                                        .setRebalance(RebalanceEnum.forValue(Integer.parseInt(cols[5])))
                                        .setBenchmarkData(new BigDecimal(cols[6]))
                                        .setPortfolioId(portfolioId)
                                        .setVooTenDays(vooTenDays)
                                        .setCreateTime(now)
                                        .setUpdateTime(now)
                                        .setId(Sequence.next());
                                PortLevel queryParam = new PortLevel();
                                queryParam.setPortfolioId(portLevel.getPortfolioId());
                                queryParam.setModelDate(portLevel.getModelDate());
                                PortLevel oldPortLevel = portLevelMapper.getPortLevel(queryParam);
                                if (null == oldPortLevel) {
                                    portLevels.add(portLevel);
                                }
                            }
                            portLevelMapper.insertBatch(portLevels);
                            lines = null;
                        } catch (Exception ex) {
                            LOGGER.error("fileUrl:{},fileName:{}, 同步走势图异常", fileUrl, fileName, ex);
                            modelRecommendService.sendFtpFileNotFound(ExceptionUtils.getMessage(ex));
                        }
                        LOGGER.info("#####fileUrl:{},fileName:{},进行走势图的同步完成", fileUrl, fileName);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("#####同步用户收益取消异常:", ex);
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
    public PortLevel getFirstPortLevel(String portfolioId) {
        return portLevelMapper.getFirstPortLevel(portfolioId);
    }

}
