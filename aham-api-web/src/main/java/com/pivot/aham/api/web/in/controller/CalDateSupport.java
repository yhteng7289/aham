package com.pivot.aham.api.web.in.controller;

import com.pivot.aham.common.core.util.DateUtils;

import java.util.Date;

public class CalDateSupport {

    public static Date getCalDate(){
        //当前时间大于16：30，返回当天，否则返回昨日
        Date now = DateUtils.now();
        Date tDate = DateUtils.getDate(new Date(),20,30,0);
        if(now.compareTo(tDate)>0){
            return now;
        }else{
            return DateUtils.addDateByDay(now,-1);
        }
    }

    public static Date getCalYesDate(){
        Date baseDate = CalDateSupport.getCalDate();
        Date calDate = DateUtils.addDateByDay(baseDate,-1);
        return calDate;
    }
}
