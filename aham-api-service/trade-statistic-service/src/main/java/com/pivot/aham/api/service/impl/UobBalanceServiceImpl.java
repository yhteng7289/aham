package com.pivot.aham.api.service.impl;

import com.google.common.collect.Maps;
import com.pivot.aham.api.server.dto.BankVirtualAccountBalDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderBalDTO;
import com.pivot.aham.api.server.dto.BankVirtualAccountOrderDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.UobBalanceService;
import com.pivot.aham.api.service.job.uob.UobConstant;
import com.pivot.aham.common.core.support.file.excel.ExportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("uobBalanceService")
@Slf4j
public class UobBalanceServiceImpl implements UobBalanceService {
    @Autowired
    private UserServiceRemoteService userServiceRemoteService;

    @Override
    public void statisExport() {
        Date nowDate = DateUtils.now();
        Date startTime=DateUtils.setHours(DateUtils.getStartDate(DateUtils.addDays(nowDate, -1)), 12);
        startTime=DateUtils.setMinutes(startTime,30);
        Date endTime=DateUtils.setHours(DateUtils.getStartDate(nowDate), 12);
        endTime=DateUtils.setMinutes(endTime,30);
        BankVirtualAccountOrderDTO bankVirtualAccountOrderDTO = new BankVirtualAccountOrderDTO();
        bankVirtualAccountOrderDTO.setStartTradeTime(startTime);
        bankVirtualAccountOrderDTO.setEndTradeTime(endTime);

        //获取昨日12点到今日12点的uob订单数据
        List<BankVirtualAccountOrderBalDTO> bankVirtualAccountOrderBalDTOS = userServiceRemoteService.getListByTradeTime(bankVirtualAccountOrderDTO);

        //获取12点到13点的uob账户余额数据
        Map<String, Object> paramss = Maps.newHashMap();
        Date startTime01=DateUtils.setHours(DateUtils.getStartDate(nowDate), 12);
        Date endTime01=DateUtils.setHours(DateUtils.getStartDate(nowDate), 13);
        paramss.put("startTime",startTime01);
        paramss.put("endTime", endTime01);
        List<BankVirtualAccountBalDTO> bankVirtualAccountBalDTOS=userServiceRemoteService.getAccountBalByTradeTime(paramss);

        ExportExcel exportExcel = new ExportExcel(null, BankVirtualAccountOrderBalDTO.class,1,"virtual_account_order");
        exportExcel.setDataList(bankVirtualAccountOrderBalDTOS);

        List<String> headerList = exportExcel.genHeaderList(BankVirtualAccountBalDTO.class, 1,new int[]{});
        exportExcel.addSheetWithName(null, headerList,"virtual_account");
        exportExcel.setDataList(bankVirtualAccountBalDTOS);

        String fileName = "UobBalance_" + DateUtils.formatDate(nowDate, "yyyy-MM-dd") + ".xlsx";;
        String ftpPath = UobConstant.getUobBalancePath() + fileName;


        try(OutputStream outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath)) {
            exportExcel.write(outputStream);
        } catch (IOException e) {
            ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }finally {
            exportExcel.dispose();
        }
    }

    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }
}
