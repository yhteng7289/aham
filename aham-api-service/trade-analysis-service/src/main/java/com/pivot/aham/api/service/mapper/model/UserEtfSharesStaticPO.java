package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户的etf资产平铺统计
 *
 * @author addison
 * @since 2019年02月22日
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_user_etf_shares_static",resultMap = "UserEtfSharesStaticRes")
public class UserEtfSharesStaticPO extends BaseModel {
    private Date staticDate;
    private Long accountId;
    private String clientId;
    private String goalId;
    
    
    private BigDecimal aagf;
    private BigDecimal oneabf;
    private BigDecimal di;
    private BigDecimal onepgf;
    private BigDecimal df;
    private BigDecimal oneaef;
    private BigDecimal asi;
    private BigDecimal aff;
    private BigDecimal aif;
    private BigDecimal onebf;
    private BigDecimal edf;
    private BigDecimal oneef;
    private BigDecimal nctf;
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

    /*private BigDecimal vt;
    private BigDecimal eem;
    private BigDecimal bndx;
    private BigDecimal shv;
    private BigDecimal emb;
    private BigDecimal vwob;
    private BigDecimal bwx;
    private BigDecimal hyg;
    private BigDecimal jnk;
    private BigDecimal mub;
    private BigDecimal lqd;
    private BigDecimal vcit;
    private BigDecimal flot;
    private BigDecimal ief;
    private BigDecimal uup;
    private BigDecimal pdbc;
    private BigDecimal gld;
    private BigDecimal vnq;
    private BigDecimal vea;
    private BigDecimal vpl;
    private BigDecimal ewa;
    private BigDecimal spy;
    private BigDecimal voo;
    private BigDecimal vti;
    private BigDecimal vgk;
    private BigDecimal ewj;
    private BigDecimal qqq;
    private BigDecimal ews;
    private BigDecimal ewz;
    private BigDecimal ashr;
    private BigDecimal vwo;
    private BigDecimal ilf;
    private BigDecimal rsx;
    private BigDecimal aaxj;*/

    private Date startStaticDate;
    private Date endStaticDate;


}
