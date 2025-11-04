package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.enums.BalanceOfAccountType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class SaxoBalOfAccNoticePO implements Serializable{
    private Long id;
    private Date noticeTime;
    private BalanceOfAccountType balanceOfAccountType;
    private String balTransNum;

    public SaxoBalOfAccNoticePO(Date noticeTime, BalanceOfAccountType balanceOfAccountType, String balTransNum) {
        this.noticeTime = noticeTime;
        this.balanceOfAccountType = balanceOfAccountType;
        this.balTransNum = balTransNum;
    }

    public SaxoBalOfAccNoticePO(Date noticeTime, BalanceOfAccountType balanceOfAccountType) {
        this.noticeTime = noticeTime;
        this.balanceOfAccountType = balanceOfAccountType;
    }

    public SaxoBalOfAccNoticePO() {
    }
}
