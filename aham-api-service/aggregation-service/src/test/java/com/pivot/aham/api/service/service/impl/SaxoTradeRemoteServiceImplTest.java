package com.pivot.aham.api.service.service.impl;

import com.google.common.collect.Sets;
import com.pivot.aham.api.service.SingaporeHolidayService;
import com.pivot.aham.api.service.mapper.model.SingaporeHolidayPO;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.DateTypeEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SaxoTradeRemoteServiceImplTest {
    @Resource
    private SingaporeHolidayService singaporeHolidayService;


    @Test
    public void isHoliday(){
        //判断是否为新加坡非交易日
        Boolean isWeekEnd = DateUtils.isWeekEnd(new Date());
        //查询节假日
        SingaporeHolidayPO singaporeHolidayPO = new SingaporeHolidayPO();
        singaporeHolidayPO.setDateType(DateTypeEnum.HOLIDAY);
        List<SingaporeHolidayPO> singaporeHolidayPOList = singaporeHolidayService.queryList(singaporeHolidayPO);
        Set<Date> dateSet = Sets.newHashSet();
        for(SingaporeHolidayPO singaporeHoliday:singaporeHolidayPOList){
            dateSet.add(singaporeHoliday.getVaDate());
        }
        Date nowDate = DateUtils.getStartDate(new Date());

        if(isWeekEnd || dateSet.contains(nowDate)){
            Assert.assertTrue(true);
        }
        Assert.assertTrue(false);
    }
}
