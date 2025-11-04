package com.pivot.aham.api.service.withdrawal;

import com.alibaba.fastjson.JSON;
import com.pivot.aham.api.server.dto.SaxoToUobOfflineConfirmByExcelDTO;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.support.file.excel.ImportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.util.DateUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfirmSaxoToUobJobImplTest {


    @Test
    public void confirmSaxoToUob() throws IOException, InvalidFormatException, IllegalAccessException, InstantiationException {
        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
        String fileName=date+"_confirm.xlsx";
        String filePath = "/output/20190715_confirm.xlsx";

        InputStream inputStream =  FTPClientUtil.getFtpInputStream(filePath);
        if(inputStream == null){
            Message.error("该文件不存在");
        }
        ImportExcel importExcel = null;
        importExcel = new ImportExcel(fileName,inputStream, 0, 0);
        List<SaxoToUobOfflineConfirmByExcelDTO> lists = importExcel.getDataList(SaxoToUobOfflineConfirmByExcelDTO.class);

        for(SaxoToUobOfflineConfirmByExcelDTO saxoToUobOfflineConfirmByExcelDTO:lists) {
            System.out.println(JSON.toJSONString(saxoToUobOfflineConfirmByExcelDTO));
        }


    }

}
