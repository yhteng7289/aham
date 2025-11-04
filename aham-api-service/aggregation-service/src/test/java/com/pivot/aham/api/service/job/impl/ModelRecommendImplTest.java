package com.pivot.aham.api.service.job.impl;

import com.google.common.collect.Lists;
import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserInfoDTO;
import com.pivot.aham.api.service.job.ModelRecommendJob;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.api.service.service.UserGoalInfoService;
import com.pivot.aham.api.service.service.UserInfoService;
import com.pivot.aham.common.core.support.file.excel.ImportExcel;
import com.pivot.aham.common.enums.CurrencyEnum;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luyang.li on 18/12/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelRecommendImplTest {

    @Resource
    private ModelRecommendJob modelRecommendJob;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private BankVirtualAccountService bankVirtualAccountService;

    @Resource
    private UserGoalInfoService userGoalInfoInfoService;

    @Test
    public void synchroModelRecommend() throws Exception {
        String date = "20190425";
        modelRecommendJob.synchroModelRecommend(date);
    }

    @Test
    public void initUserBaseInfo() throws IOException, InvalidFormatException, IllegalAccessException, InstantiationException {
        String fileName = "/Users/luyang.li/IdeaProjects/aham/aham-api-service/aggregation-service/src/test/java/com/pivot/aham/api/service/job/impl/setupdata-DAAS-默认account.xlsx";
//        File file = new File(fileName);

        InputStream inputStream = new FileInputStream(fileName);
        ImportExcel importExcel = new ImportExcel(fileName, inputStream, 0, 0);
        List<InitUserBaseExcelDTO> lists = importExcel.getDataList(InitUserBaseExcelDTO.class);

        for (InitUserBaseExcelDTO dto : lists) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setClientId(dto.getClientId());
            userInfoDTO.setClientName(dto.getClientName());
            userInfoService.saveUserInfo(userInfoDTO);

            List<BankVirtualAccountDTO> virtualAccountList = Lists.newArrayList();
            BankVirtualAccountDTO bankVirtualAccountGsd = new BankVirtualAccountDTO();
            bankVirtualAccountGsd.setVirtualAccountNo(dto.getVirtualAccountNoSGD());
            bankVirtualAccountGsd.setClientId(dto.getClientId());
            bankVirtualAccountGsd.setCurrency(CurrencyEnum.SGD);
            bankVirtualAccountGsd.setClientName(dto.getClientName());
            bankVirtualAccountGsd.setCashAmount(BigDecimal.ZERO);
            bankVirtualAccountGsd.setUsedAmount(BigDecimal.ZERO);
            bankVirtualAccountGsd.setFreezeAmount(BigDecimal.ZERO);
            virtualAccountList.add(bankVirtualAccountGsd);

            BankVirtualAccountDTO bankVirtualAccountUsd = new BankVirtualAccountDTO();
            bankVirtualAccountUsd.setVirtualAccountNo(dto.getVirtualAccountNoUSD());
            bankVirtualAccountUsd.setClientId(dto.getClientId());
            bankVirtualAccountUsd.setCurrency(CurrencyEnum.USD);
            bankVirtualAccountUsd.setClientName(dto.getClientName());
            bankVirtualAccountUsd.setCashAmount(BigDecimal.ZERO);
            bankVirtualAccountUsd.setUsedAmount(BigDecimal.ZERO);
            bankVirtualAccountUsd.setFreezeAmount(BigDecimal.ZERO);
            virtualAccountList.add(bankVirtualAccountUsd);
            bankVirtualAccountService.saveBatch(virtualAccountList);

            List<UserGoalInfoDTO> userGoalInfoDTOS = Lists.newArrayList();
            UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
            userGoalInfoDTO.setGoalId(dto.getGoalId());
            userGoalInfoDTO.setClientId(dto.getClientId());
            userGoalInfoDTO.setPortfolioId(dto.getPortfolioID());
            userGoalInfoDTO.setReferenceCode(dto.getReferenceCode());
            userGoalInfoDTOS.add(userGoalInfoDTO);
            userGoalInfoInfoService.saveUserGoalInfos(userGoalInfoDTOS);

        }
    }

}