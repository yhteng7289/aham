/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.excel;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import sg.com.aham.finance.utility.excel.ExcelField;

/**
 *
 * @author HP
 */
@Data

public class UserFundNavExcelData {

    @ExcelField(title = "ACCOUNT_ID")
    private Long accountId;
    @ExcelField(title = "CLIENT_ID")
    private int clientId;
    @ExcelField(title = "GOAL_ID")
    private String goalId;
    @ExcelField(title = "FUND_NAV")
    private BigDecimal fundNav;
    @ExcelField(title = "TOTAL_SHARE")
    private BigDecimal totalShare;
    @ExcelField(title = "TOTAL_ASSET")
    private BigDecimal totalAsset;
    @ExcelField(title = "NAV_TIME")
    private Date navTime;
    @ExcelField(title = "CREATE_TIME")
    private Date createTime;
    @ExcelField(title = "UPDATE_TIME")
    private Date updateTime;
    @ExcelField(title = "USD_TO_SGD")
    private BigDecimal usdToSgd;
    @ExcelField(title = "RATE_DATE")
    private String rateDate;
}
