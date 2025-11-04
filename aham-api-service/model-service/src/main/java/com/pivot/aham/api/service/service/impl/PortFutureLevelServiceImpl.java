package com.pivot.aham.api.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.pivot.aham.api.service.mapper.PortFutureLevelMapper;
import com.pivot.aham.api.service.mapper.model.PortFutureLevelPO;
import com.pivot.aham.api.service.service.PortFutureLevelService;
import com.pivot.aham.common.core.Constants;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.file.ftp.SftpClient;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.enums.AgeLevelEnum;
import com.pivot.aham.common.enums.PoolingEnum;
import com.pivot.aham.common.enums.PortFutureStatusEnum;
import com.pivot.aham.common.enums.RiskLevelEnum;
import java.io.BufferedReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
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
 * Created by luyang.li on 19/2/22.
 */
@Service
@Slf4j
public class PortFutureLevelServiceImpl extends BaseServiceImpl<PortFutureLevelPO, PortFutureLevelMapper> implements PortFutureLevelService {

    @Resource
    private ModelRecommendSupport modelRecommendSupport;

    //portFutureLevel_pool1_risk4_age5.csv
    @Override
    public void synchroPortFutureLevel(Date date) {

        List<String> errorMessages = Lists.newArrayList();
        String fileUrl = Constants.FTP_BASE_FOLDER + "/PORTLEVEL/";
        SftpClient sftpClient = SftpClient.connect("3.0.163.17", 22, "ftpuser", "OmMsi93DBcNo", 5000, 10);
        InputStream stream = null;
        try {

            for (PoolingEnum poolingEnum : PoolingEnum.values()) {
                for (RiskLevelEnum riskEnum : RiskLevelEnum.values()) {
                    for (AgeLevelEnum ageEnum : AgeLevelEnum.values()) {
                        try {
                            String fileName = "portFutureLevel_" + poolingEnum.getName().toLowerCase() + "_"
                                    + riskEnum.getName().toLowerCase() + "_" + ageEnum.getName().toLowerCase() + ".csv";
                            String filePath = fileUrl + fileName;
                            log.info("#####pooling:{},riskLevel:{},ageLevel:{},同步未来预期收益,filePath:{}", poolingEnum.getDesc(), riskEnum.getDesc(), ageEnum.getDesc(), filePath);

                            List<String> lines = new ArrayList();
                            try {
                                stream = sftpClient.get(filePath);
                                String thisLine = "";
                                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                                while ((thisLine = br.readLine()) != null) {
                                    lines.add(thisLine);
                                }

                                // remove header;
                                lines.remove(0);
                            } finally {

                            }

                            List<PortFutureLevelPO> portFutureLevelPOLists = Lists.newArrayList();
                            String portfolioId = "P" + poolingEnum.getValue() + "R" + riskEnum.getValue() + "A" + ageEnum.getValue();
                            for (String line : lines) {
                                String[] cols = line.split(",");
                                Date modelDate = DateUtils.parseDate(cols[0], DateUtils.DATE_FORMAT3);
                                PortFutureLevelPO portFutureLevel = new PortFutureLevelPO();
                                portFutureLevel.setModelDate(modelDate);
                                portFutureLevel.setId(Sequence.next());
                                portFutureLevel.setNinetyFiveLow(new BigDecimal(cols[3]));
                                portFutureLevel.setNinetyFiveUp(new BigDecimal(cols[4]));
                                portFutureLevel.setSixtyEightLow(new BigDecimal(cols[2]));
                                portFutureLevel.setSixtyEightUp(new BigDecimal(cols[5]));
                                portFutureLevel.setRcmd(new BigDecimal(cols[1]));
                                portFutureLevel.setStatus(PortFutureStatusEnum.Effective);
                                portFutureLevel.setCreateTime(DateUtils.now());
                                portFutureLevel.setUpdateTime(DateUtils.now());
                                portFutureLevel.setPortfolioId(portfolioId);
                                portFutureLevelPOLists.add(portFutureLevel);
                            }
                            //历史设置失效,设置目前为可用
                            PortFutureLevelPO queryParam = new PortFutureLevelPO();
                            queryParam.setPortfolioId(portfolioId);
                            queryParam.setStatus(PortFutureStatusEnum.Effective);
                            List<PortFutureLevelPO> portFutureLevels = mapper.listPortFutureLevel(queryParam);
                            modelRecommendSupport.handelPortFutureLevels(portFutureLevels, portFutureLevelPOLists);

                        } catch (Exception ex) {
                            log.error("#####pooling:{},riskLevel:{},ageLevel:{},同步未来预期收益异常:",
                                    poolingEnum.getDesc(), riskEnum.getDesc(), ageEnum.getDesc(), ex);
                            errorMessages.add(ex.getMessage());
                        }
                    }
                }
            }
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
        //发送报警邮件在循环外控制
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            ErrorLogAndMailUtil.logError(log, errorMessages);
        }
    }

    @Override
    public List<PortFutureLevelPO> queryPortFutureLevel(PortFutureLevelPO portFutureLevel) {
        return mapper.listPortFutureLevel(portFutureLevel);
    }
}
