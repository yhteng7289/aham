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
public class UserAssetReport {

    private Long accountId;

    private int clientId;

    private String productCode;

    private BigDecimal share;

    private BigDecimal money;

    private Date createTime;

    private Date updateTime;

    private String goalId;

    private BigDecimal usdToSgd;

    private String rateDate;

}
