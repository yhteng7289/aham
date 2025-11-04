package com.pivot.aham.api.service.job.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pivot.aham.api.server.dto.SaxoToUobOfflineConfirmByExcelDTO;
import com.pivot.aham.api.server.remoteservice.WithdrawalRemoteService;
import com.pivot.aham.api.service.job.ConfirmSaxoToUobJob;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.file.excel.ImportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * 每日确认saxo到uob线下转账
 *
 * @author addison
 * @since 2019年02月19日
 */
/*@ElasticJobConf(name = "ConfirmSaxoToUobJob_2",
        cron = "0 15 16 * * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount = 1,
        description = "交易06_交易分析#确认SAXO到UOB的转账指令", eventTraceRdbDataSource = "dataSource")
@Slf4j
public class ConfirmSaxoToUobJobImpl implements SimpleJob, ConfirmSaxoToUobJob {
    @Resource
    private WithdrawalRemoteService withdrawalRemoteService;

    @Override
    public void confirmSaxoToUob() {
        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName = date + "_confirm.xlsx";
        String filePath = PropertiesUtil.getString("ftp.pivot.confirm") + "/confirm/saxoOfflineConfirm/" + fileName;

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        if (inputStream == null) {
            Message.error("该文件不存在");
        }
        ImportExcel importExcel = null;
        try {
            importExcel = new ImportExcel(fileName, inputStream, 0, 0);
            List<SaxoToUobOfflineConfirmByExcelDTO> lists = importExcel.getDataList(SaxoToUobOfflineConfirmByExcelDTO.class);

            log.info("saxoToUob execl data : {}", lists);
            for (SaxoToUobOfflineConfirmByExcelDTO saxoToUobOfflineConfirmByExcelDTO : lists) {
                withdrawalRemoteService.saxoToUobOfflineConfirmByExcel(saxoToUobOfflineConfirmByExcelDTO);
            }
        } catch (InvalidFormatException | IOException | IllegalAccessException | InstantiationException e) {
            log.error("saxo to uob 确认任务异常", e);
            throw new BusinessException(e);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            confirmSaxoToUob();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
    }
}*/
