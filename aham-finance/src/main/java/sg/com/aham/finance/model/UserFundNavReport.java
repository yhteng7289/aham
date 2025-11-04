/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author HP
 */
@Data
public class UserFundNavReport {

    private Long accountId;

    private int clientId;

    private String goalId;

    private BigDecimal fundNav;

    private BigDecimal totalShare;

    private BigDecimal totalAsset;

    private Date navTime;

    private Date createTime;

    private Date updateTime;

    private BigDecimal usdToSgd;

    private String rateDate;
}
