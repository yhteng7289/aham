/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.service.core;

import com.google.common.collect.Lists;
import com.pivot.aham.api.service.mapper.model.UserAssetPO;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserEtfSharesStaticPO;
import com.pivot.aham.api.service.service.UserEtfSharesService;
import com.pivot.aham.api.service.service.UserEtfSharesStaticService;
import com.pivot.aham.common.core.Constants;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author HP
 */
@Slf4j
public class HandleEtf implements Runnable {

    private UserEtfSharesService userEtfSharesService = null;
    private UserEtfSharesStaticService userEtfSharesStaticService = null;
    private List<UserAssetPO> userAssetPOs = null;
    private Date date = null;

    public HandleEtf(List<UserAssetPO> userAssetPOs, Date date, UserEtfSharesService userEtfSharesService, UserEtfSharesStaticService userEtfSharesStaticService) {
        this.userAssetPOs = userAssetPOs;
        this.date = date;
        this.userEtfSharesService = userEtfSharesService;
        this.userEtfSharesStaticService = userEtfSharesStaticService;
    }

    @Override
    public void run() {
        List<UserEtfSharesStaticPO> userEtfSharesStaticList = Lists.newArrayList();
        log.info("HandleEtf thread Running");
        for (UserAssetPO userAsset : userAssetPOs) {
            UserEtfSharesPO userEtfSharesPO = new UserEtfSharesPO();
            userEtfSharesPO.setAccountId(userAsset.getAccountId());
            userEtfSharesPO.setClientId(userAsset.getClientId());
            userEtfSharesPO.setGoalId(userAsset.getGoalId());
            userEtfSharesPO.setProductCode(userAsset.getProductCode());
            userEtfSharesPO.setMoney(userAsset.getMoney());
            userEtfSharesPO.setShares(userAsset.getShare());
            if (userAsset.getProductCode().equals(Constants.CASH)) {
                userEtfSharesPO.setShares(BigDecimal.ZERO);
            }

            if (date == null) {
                userEtfSharesPO.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
            }

            UserEtfSharesPO userEtfSharesQuery = new UserEtfSharesPO();
            userEtfSharesQuery.setAccountId(userAsset.getAccountId());
            userEtfSharesQuery.setClientId(userAsset.getClientId());
            userEtfSharesQuery.setGoalId(userAsset.getGoalId());
            userEtfSharesQuery.setProductCode(userAsset.getProductCode());
            if (date == null) {
                userEtfSharesQuery.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesQuery.setStaticDate(date);
            }

            UserEtfSharesPO userEtfShares = userEtfSharesService.selectByStaticDate(userEtfSharesQuery);
            if (userEtfShares != null) {
                userEtfSharesPO.setId(userEtfShares.getId());
            }
            userEtfSharesService.updateOrInsert(userEtfSharesPO);

            UserEtfSharesStaticPO userEtfSharesStaticPO = new UserEtfSharesStaticPO();
            userEtfSharesStaticPO.setAccountId(userAsset.getAccountId());
            userEtfSharesStaticPO.setClientId(userAsset.getClientId());
            userEtfSharesStaticPO.setGoalId(userAsset.getGoalId());
            if (date == null) {
                userEtfSharesStaticPO.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesStaticPO.setStaticDate(date);
            }
            
            if (userAsset.getProductCode().equals("AAGF")) {
                userEtfSharesStaticPO.setAagf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1ABF")) {
                userEtfSharesStaticPO.setOneabf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("DI")) {
                userEtfSharesStaticPO.setDi(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1PGF")) {
                userEtfSharesStaticPO.setOnepgf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("DF")) {
                userEtfSharesStaticPO.setDf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1AEF")) {
                userEtfSharesStaticPO.setOneaef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ASI")) {
                userEtfSharesStaticPO.setAsi(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AFF")) {
                userEtfSharesStaticPO.setAff(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AIF")) {
                userEtfSharesStaticPO.setAif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1BF")) {
                userEtfSharesStaticPO.setOnebf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EDF")) {
                userEtfSharesStaticPO.setEdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("1EF")) {
                userEtfSharesStaticPO.setOneef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("NCTF")) {
                userEtfSharesStaticPO.setNctf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GOF")) {
                userEtfSharesStaticPO.setGof(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SCF")) {
                userEtfSharesStaticPO.setScf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SAPBF")) {
                userEtfSharesStaticPO.setSapbf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SAPDF")) {
                userEtfSharesStaticPO.setSapdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GIF")) {
                userEtfSharesStaticPO.setGif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BAL")) {
                userEtfSharesStaticPO.setBal(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BOND")) {
                userEtfSharesStaticPO.setBond(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SDF")) {
                userEtfSharesStaticPO.setSdf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SIF")) {
                userEtfSharesStaticPO.setSif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SOF")) {
                userEtfSharesStaticPO.setSof(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SGDIF")) {
                userEtfSharesStaticPO.setSgdif(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SGTF")) {
                userEtfSharesStaticPO.setSgtf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GDIFMYRH")) {
                userEtfSharesStaticPO.setGdifmyrh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLIFMYRNH")) {
                userEtfSharesStaticPO.setGlifmyrnh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLIFMYR")) {
                userEtfSharesStaticPO.setGlifmyr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("WSGQFMYR")) {
                userEtfSharesStaticPO.setWsgqfmyr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("WSGQFMYRH")) {
                userEtfSharesStaticPO.setWsgqfmyrh(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("CF")) {
                userEtfSharesStaticPO.setCf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SJQFMYRNH")) {
                userEtfSharesStaticPO.setSjqfmyrnh(userAsset.getShare());
            }
/*
            if (userAsset.getProductCode().equals("VT")) {
                userEtfSharesStaticPO.setVt(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EEM")) {
                userEtfSharesStaticPO.setEem(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BNDX")) {
                userEtfSharesStaticPO.setBndx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SHV")) {
                userEtfSharesStaticPO.setShv(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EMB")) {
                userEtfSharesStaticPO.setEmb(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VWOB")) {
                userEtfSharesStaticPO.setVwob(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("BWX")) {
                userEtfSharesStaticPO.setBwx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("HYG")) {
                userEtfSharesStaticPO.setHyg(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("JNK")) {
                userEtfSharesStaticPO.setJnk(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("MUB")) {
                userEtfSharesStaticPO.setMub(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("LQD")) {
                userEtfSharesStaticPO.setLqd(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VCIT")) {
                userEtfSharesStaticPO.setVcit(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("FLOT")) {
                userEtfSharesStaticPO.setFlot(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("IEF")) {
                userEtfSharesStaticPO.setIef(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("UUP")) {
                userEtfSharesStaticPO.setUup(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("PDBC")) {
                userEtfSharesStaticPO.setPdbc(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("GLD")) {
                userEtfSharesStaticPO.setGld(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VNQ")) {
                userEtfSharesStaticPO.setVnq(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VEA")) {
                userEtfSharesStaticPO.setVea(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VPL")) {
                userEtfSharesStaticPO.setVpl(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWA")) {
                userEtfSharesStaticPO.setEwa(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("SPY")) {
                userEtfSharesStaticPO.setSpy(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VOO")) {
                userEtfSharesStaticPO.setVoo(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VTI")) {
                userEtfSharesStaticPO.setVti(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VGK")) {
                userEtfSharesStaticPO.setVgk(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWJ")) {
                userEtfSharesStaticPO.setEwj(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("QQQ")) {
                userEtfSharesStaticPO.setQqq(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWS")) {
                userEtfSharesStaticPO.setEws(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("EWZ")) {
                userEtfSharesStaticPO.setEwz(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ASHR")) {
                userEtfSharesStaticPO.setAshr(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("VWO")) {
                userEtfSharesStaticPO.setVwo(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("ILF")) {
                userEtfSharesStaticPO.setIlf(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("RSX")) {
                userEtfSharesStaticPO.setRsx(userAsset.getShare());
            }
            if (userAsset.getProductCode().equals("AAXJ")) {
                userEtfSharesStaticPO.setAaxj(userAsset.getShare());
            }
*/
            userEtfSharesStaticList.add(userEtfSharesStaticPO);

            UserEtfSharesStaticPO userEtfSharesStaticQuery = new UserEtfSharesStaticPO();
            if (date == null) {
                userEtfSharesStaticQuery.setStaticDate(new Date());
            } else {
                userEtfSharesPO.setStaticDate(date);
                userEtfSharesStaticQuery.setStaticDate(date);
            }
            userEtfSharesStaticQuery.setAccountId(userAsset.getAccountId());
            userEtfSharesStaticQuery.setClientId(userAsset.getClientId());
            userEtfSharesStaticQuery.setGoalId(userAsset.getGoalId());
            UserEtfSharesStaticPO userEtfSharesStatic = userEtfSharesStaticService.selectByStaticDate(userEtfSharesStaticQuery);
            if (userEtfSharesStatic != null) {
                userEtfSharesStaticPO.setId(userEtfSharesStatic.getId());
            }
            userEtfSharesStaticService.updateOrInsert(userEtfSharesStaticPO);
        }
    }

}
