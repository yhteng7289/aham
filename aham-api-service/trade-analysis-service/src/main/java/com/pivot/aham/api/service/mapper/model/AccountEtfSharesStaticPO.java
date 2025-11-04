package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2019年02月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_account_etf_shares_static",resultMap = "AccountEtfSharesStaticRes")
public class AccountEtfSharesStaticPO extends BaseModel {
    private Date staticDate;
    private Long accountId;
    private BigDecimal aagf;
    private BigDecimal oneabf;
    private BigDecimal di;
    private BigDecimal df;
    private BigDecimal oneaef;
    private BigDecimal asi;
    private BigDecimal aff;
    private BigDecimal aif;
    private BigDecimal onebf;
    private BigDecimal edf;
    private BigDecimal oneef;
    private BigDecimal nctf;
    private BigDecimal onepgf;
    private BigDecimal gof;
    private BigDecimal scf;
    private BigDecimal sapbf;
    private BigDecimal sapdf;
    private BigDecimal gif;
    private BigDecimal bal;
    private BigDecimal bond;
    private BigDecimal sdf;
    private BigDecimal sif;
    private BigDecimal sof;
    private BigDecimal sgdif;
    private BigDecimal sgtf;
    private BigDecimal gdifmyrh;
    private BigDecimal glifmyrnh;
    private BigDecimal glifmyr;
    private BigDecimal wsgqfmyr;
    private BigDecimal wsgqfmyrh;
    private BigDecimal cf;
    private BigDecimal sjqfmyrnh;
   // private BigDecimal ashr;
   // private BigDecimal vwo;
   // private BigDecimal ilf;
   // private BigDecimal rsx;
   // private BigDecimal aaxj;
//    private BigDecimal asx;
//    private BigDecimal awc;

}
