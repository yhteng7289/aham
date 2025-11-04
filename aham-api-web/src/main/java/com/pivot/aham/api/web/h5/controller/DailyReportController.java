/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.h5.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.PivotFeeDetailDTO;
import com.pivot.aham.api.server.dto.ProductInfoResDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionReqDTO;
import com.pivot.aham.api.server.dto.SaxoShareOpenPositionResDTO;
import com.pivot.aham.api.server.dto.req.AccountInfoReqDTO;
import com.pivot.aham.api.server.dto.req.AccountStaticsReqDTO;
import com.pivot.aham.api.server.dto.req.AccountetfSharesReqDTO;
import com.pivot.aham.api.server.dto.req.ClosingPriceReq;
import com.pivot.aham.api.server.dto.req.PivotPftAssetResDTO;
import com.pivot.aham.api.server.dto.res.AccountInfoResDTO;
import com.pivot.aham.api.server.dto.res.AccountStaticsResDTO;
import com.pivot.aham.api.server.dto.res.AccountetfSharesResDTO;
import com.pivot.aham.api.server.dto.resp.ClosingPriceResult;
import com.pivot.aham.api.server.remoteservice.AccountEtfSharesRemoteService;
import com.pivot.aham.api.server.remoteservice.AccountInfoRemoteService;
import com.pivot.aham.api.server.remoteservice.AccountStaticsRemoteService;
import com.pivot.aham.api.server.remoteservice.AssetServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.ModelServiceRemoteService;
import com.pivot.aham.api.server.remoteservice.PivotFeeDetailRemoteService;
import com.pivot.aham.api.server.remoteservice.PivotPftRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoStatisticRemoteService;
import com.pivot.aham.api.server.remoteservice.SaxoTradeRemoteService;
import com.pivot.aham.api.web.in.vo.AccountEtfResVo;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.util.CalDecimal;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.analysis.FeeTypeEnum;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author HP
 */
@Controller
@CrossOrigin(value = "*")
@RequestMapping("/api/v1/")
@Api(value = "日报接口", description = "日报接口")
@Slf4j
public class DailyReportController extends AbstractController {

    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;

    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;

    @Resource
    private AccountInfoRemoteService accountInfoRemoteService;

    @Resource
    private SaxoStatisticRemoteService saxoStatisticRemoteService;

    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;

    @Resource
    private PivotPftRemoteService pivotPftRemoteService;

    @Resource
    private AccountStaticsRemoteService accountStaticsRemoteService;

    @Resource
    private PivotFeeDetailRemoteService pivotFeeDetailRemoteService;

    @Resource
    private AccountEtfSharesRemoteService accountEtfSharesRemoteService;

    @Resource
    private RedissonHelper redissonHelper;

    @RequestMapping("h5/dailyReport")
    @ApiOperation(value = "WEB下载日报", produces = MediaType.APPLICATION_JSON_VALUE)
    public void dailyReport(@RequestParam("date") String date, HttpServletResponse response, HttpServletRequest request) throws Exception {

        Date startDate = null;
        Date endDate = null;
        String fileName = "daily-report-" + date + ".xlsx";
        String filePath = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strStartDate = date + " 00:00:00";
            String strEndDate = date + " 23:59:59";
            startDate = sdf.parse(strStartDate);
            endDate = sdf.parse(strEndDate);
        } catch (ParseException e) {
            throw new BusinessException("Unable to parse Date");
        }

        XSSFWorkbook workbook = new XSSFWorkbook();; // new HSSFWorkbook() for generating `.xls` file

        try {
            CreationHelper createHelper = workbook.getCreationHelper();
            XSSFSheet sheet = workbook.createSheet();
            // 
            Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Date
            Cell cellA1 = headerRow.createCell(0);
            cellA1.setCellValue(date);
            cellA1.setCellStyle(headerCellStyle);

            // Saxo
            Cell cellA2 = headerRow.createCell(1);
            cellA2.setCellValue("SAXO\t");
            cellA2.setCellStyle(headerCellStyle);

            // Price
            Cell cellA3 = headerRow.createCell(2);
            cellA3.setCellValue("Price\t");
            cellA3.setCellStyle(headerCellStyle);

            // Value
            Cell cellA4 = headerRow.createCell(3);
            cellA4.setCellValue("Value\t");
            cellA4.setCellStyle(headerCellStyle);

            //获取productinfo
            List<ProductInfoResDTO> productInfoResDTOList = modelServiceRemoteService.queryAllProductInfo();
            Map<String, ProductInfoResDTO> mapProduct = productInfoResDTOList
                    .stream().collect(Collectors.toMap(ProductInfoResDTO::getProductCode, item -> item));

            Map<String, ProductInfoResDTO> sortMapProduct = new TreeMap<>(mapProduct);
            Map<String, AccountInfoResDTO> accountInfoMap = this.getAccountInfo();
            accountInfoMap = new TreeMap<>(accountInfoMap); // sort the account ID

            HashMap totalEtfSumm = new HashMap();
            HashMap cashMap = new HashMap();

            Double pftCash = 0.00;
            Double totalMgtFee = 0.00;
            Double totalMgtGstFee = 0.00;
            Double totalCustodianFee = 0.00;
            Double totalCash = 0.00;
            // Get Pft Records
            Map<String, PivotPftAssetResDTO> pivotPftAssetMap = getPftAccountAsset(endDate);
            int accountInfoAcount = 4;
            for (Map.Entry<String, AccountInfoResDTO> entry : accountInfoMap.entrySet()) {
                Double totalCashByAccount = 0.00;
                Cell accountCell = headerRow.createCell(accountInfoAcount);
                accountCell.setCellValue(entry.getKey());
                accountCell.setCellType(1); // Set as String
                accountCell.setCellStyle(headerCellStyle);
                sheet.autoSizeColumn(accountInfoAcount);

                AccountInfoResDTO _accountInfoResDTO = entry.getValue();
                Long accountId = _accountInfoResDTO.getId();
                Map<String, AccountetfSharesResDTO> accountAssetMap = this.getAccountEtfShares(accountId, endDate);
                Map<String, AccountetfSharesResDTO> sortMapAccountAsset = new TreeMap<>(accountAssetMap);

                for (Map.Entry<String, AccountetfSharesResDTO> _entry : sortMapAccountAsset.entrySet()) {
                    AccountetfSharesResDTO accountetfSharesResDTO = _entry.getValue();
                    if (_entry.getKey().equalsIgnoreCase("cash") || _entry.getKey().equalsIgnoreCase("cash1") || _entry.getKey().equalsIgnoreCase("unbuycash")) {
                        totalCashByAccount += accountetfSharesResDTO.getMoney().doubleValue();
                        if (pivotPftAssetMap.containsKey("cash")) {
                            // Value Column
                            PivotPftAssetResDTO pivotPftAssetResDTO = (PivotPftAssetResDTO) pivotPftAssetMap.get("cash");
                            pftCash = pivotPftAssetResDTO.getConfirmMoney().doubleValue();
                        }
                    } else {
                        redissonHelper.set(accountetfSharesResDTO.getAccountId() + ":" + accountetfSharesResDTO.getProductCode(), accountetfSharesResDTO.getShares().doubleValue());
                        if (totalEtfSumm.containsKey(accountetfSharesResDTO.getProductCode())) {
                            double accountEtfShare = (double) totalEtfSumm.get(accountetfSharesResDTO.getProductCode());
                            double newValue = accountEtfShare + accountetfSharesResDTO.getShares().doubleValue();
                            totalEtfSumm.replace(accountetfSharesResDTO.getProductCode(), newValue);
                        } else {
                            totalEtfSumm.put(accountetfSharesResDTO.getProductCode(), accountetfSharesResDTO.getShares().doubleValue());
                        }
                    }
                }
                String key = accountId + ":cash";
                totalCash += totalCashByAccount;
                cashMap.put(key, totalCashByAccount);

                accountInfoAcount++;
            }

            Integer mgtColumnPos = accountInfoAcount;
            Cell cellMgt = headerRow.createCell(accountInfoAcount);
            cellMgt.setCellValue("MGT\t");
            cellMgt.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(accountInfoAcount);

            Integer mgtGstColumnPos = accountInfoAcount + 1;
            Cell cellGst = headerRow.createCell(accountInfoAcount + 1);
            cellGst.setCellValue("GST\t");
            cellGst.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(accountInfoAcount + 1);

            Integer custodianColumnPos = accountInfoAcount + 2;
            Cell cellCustodi = headerRow.createCell(accountInfoAcount + 2);
            cellCustodi.setCellValue("CUSTODIAN\t");
            cellCustodi.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(accountInfoAcount + 2);

            // Usage to sum all the etf amount on different account;
            Integer sumaColumnPos = accountInfoAcount + 3;
            Cell cellSuma = headerRow.createCell(accountInfoAcount + 3);
            cellSuma.setCellValue("SUM\t");
            cellSuma.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(accountInfoAcount + 3);

            Integer pftColumnPos = accountInfoAcount + 4;
            Cell cellPft = headerRow.createCell(accountInfoAcount + 4);
            cellPft.setCellValue("PFT\t");
            cellPft.setCellStyle(headerCellStyle);
            sheet.autoSizeColumn(accountInfoAcount + 4);

            int productCodeCount = 1;
            Map<String, SaxoShareOpenPositionResDTO> saxoShareOpenPositionMap = getSaxoShareOpenPosition(startDate, endDate);

            // Make the cash to populate at last
            sortMapProduct.remove("CASH");
            sortMapProduct.remove("Cash1");
            // Make the cash to populate at last

            Double totalPftAssetValue = 0.00;

            for (Map.Entry<String, ProductInfoResDTO> entry : sortMapProduct.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("cash") || entry.getKey().equalsIgnoreCase("cash1") || entry.getKey().equalsIgnoreCase("unbuycash")) {

                } else {
                    Row _headerRow = sheet.createRow(productCodeCount);
                    Cell etfCell = _headerRow.createCell(0);
                    ProductInfoResDTO productInfoResDTO = entry.getValue();

                    String productCode = productInfoResDTO.getProductCode();

                    etfCell.setCellValue(productCode);
                    etfCell.setCellStyle(headerCellStyle);

                    Cell saxoShareOpenPositionCell = _headerRow.createCell(1);
                    Cell etfPriceCell = _headerRow.createCell(2);
                    Cell valueCell = _headerRow.createCell(3);
                    Cell sumaCell = _headerRow.createCell(sumaColumnPos);
                    Cell pftCell = _headerRow.createCell(pftColumnPos);
                    SaxoShareOpenPositionResDTO saxoShareOpenPositionResDTO = saxoShareOpenPositionMap.get(productCode);
                    Integer saxoEtfHolding = 0;
                    // Query the price
                    ClosingPriceReq closingPriceReq = new ClosingPriceReq();

                    List<String> etfObject = new ArrayList();
                    etfObject.add(productCode);
                    Date yesterday = DateUtils.addDateByDay(endDate, -1);
                    closingPriceReq.setEtfCodeList(etfObject);
                    closingPriceReq.setDate(yesterday);
                    RpcMessage<ClosingPriceResult> rpcEtfPrice = saxoTradeRemoteService.queryClosingPrice(closingPriceReq);
                    ClosingPriceResult closingPriceResult = rpcEtfPrice.getContent();
                    if (saxoShareOpenPositionResDTO == null) {
                        saxoShareOpenPositionCell.setCellType(0);
                        saxoShareOpenPositionCell.setCellValue(0);
                    } else {
                        saxoShareOpenPositionCell.setCellType(0);
                        saxoShareOpenPositionCell.setCellValue(saxoShareOpenPositionResDTO.getSaxoHoldShare().intValue());
                        saxoEtfHolding = saxoShareOpenPositionResDTO.getSaxoHoldShare().intValue();
                    }
                    saxoShareOpenPositionCell.setCellStyle(headerCellStyle);
                    BigDecimal etfPrice = closingPriceResult.getClosingPriceItemList().get(0).getPrice();
                    // Etf Price Column
                    etfPriceCell.setCellType(0);
                    etfPriceCell.setCellValue(etfPrice.doubleValue());
                    etfPriceCell.setCellStyle(headerCellStyle);

                    // Value Column
                    valueCell.setCellType(0);
                    valueCell.setCellValue(etfPrice.doubleValue() * saxoEtfHolding);
                    valueCell.setCellStyle(headerCellStyle);

                    // Suma Column                   
                    if (totalEtfSumm.containsKey(productCode)) {
                        double sumShare = (double) totalEtfSumm.get(productCode);
                        sumaCell.setCellValue(sumShare);
                    } else {
                        sumaCell.setCellValue(0.00);
                    }
                    sumaCell.setCellType(0);
                    sumaCell.setCellStyle(headerCellStyle);

                    if (pivotPftAssetMap.containsKey(productCode)) {
                        // Value Column
                        PivotPftAssetResDTO pivotPftAssetResDTO = (PivotPftAssetResDTO) pivotPftAssetMap.get(productCode);
                        pftCell.setCellType(0);
                        pftCell.setCellValue(pivotPftAssetResDTO.getConfirmShare().doubleValue());
                        pftCell.setCellStyle(headerCellStyle);
                        totalPftAssetValue += pivotPftAssetResDTO.getConfirmShare().doubleValue() * etfPrice.doubleValue();

                    } else {
                        pftCell.setCellType(0);
                        pftCell.setCellValue(0.00);
                        pftCell.setCellStyle(headerCellStyle);
                    }
                    productCodeCount++;
                }
            }

            int _accountInfoAcount = 4;
            for (Map.Entry<String, AccountInfoResDTO> accountEntry : accountInfoMap.entrySet()) {
                int _productCodeCount = 1;
                for (Map.Entry<String, ProductInfoResDTO> entry : sortMapProduct.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("cash") || entry.getKey().equalsIgnoreCase("cash1") || entry.getKey().equalsIgnoreCase("unbuycash")) {
                        // Do Nothing here 
                    } else {
                        Row _headerRow = null;
                        _headerRow = sheet.getRow(_productCodeCount);
                        if (_headerRow == null) {
                            _headerRow = sheet.createRow(_productCodeCount);
                        }
                        Cell accountAssetCell = _headerRow.createCell(_accountInfoAcount);

                        AccountInfoResDTO accountInfoResDTO = accountEntry.getValue();

                        if (redissonHelper.exists("" + accountInfoResDTO.getId() + ":" + entry.getKey())) {
                            double shares = redissonHelper.get("" + accountInfoResDTO.getId() + ":" + entry.getKey());
                            accountAssetCell.setCellType(0);
                            accountAssetCell.setCellValue(shares);
                            accountAssetCell.setCellStyle(headerCellStyle);
                            redissonHelper.del("" + accountInfoResDTO.getId() + ":" + entry.getKey());
                        } else {
                            accountAssetCell.setCellType(0);
                            accountAssetCell.setCellValue(0);
                            accountAssetCell.setCellStyle(headerCellStyle);
                        }

                    }
                    _productCodeCount++;
                }
                _accountInfoAcount++;
            }

            // Cash Part
            int cashColumnStaringRow = sortMapProduct.size() + 1;
            Row cashRow = sheet.createRow(cashColumnStaringRow++);
            Cell cashLabelCell = cashRow.createCell(0);
            cashLabelCell.setCellValue("cash");
            cashLabelCell.setCellType(1); // Set as String
            cashLabelCell.setCellStyle(headerCellStyle);

            accountInfoAcount = 4; // reset to column 4;
            for (Map.Entry<String, AccountInfoResDTO> accountEntry : accountInfoMap.entrySet()) {
                Cell cashPerAccount = cashRow.createCell(accountInfoAcount);

                AccountInfoResDTO accountInfoResDTO = accountEntry.getValue();
                String key = accountInfoResDTO.getId() + ":cash";
                if (cashMap.containsKey(key)) {
                    Double accountCash = (Double) cashMap.get(key);
                    cashPerAccount.setCellValue(accountCash);
                } else {
                    cashPerAccount.setCellValue(0.00);
                }
                cashPerAccount.setCellType(0); // Set as Numeric
                cashPerAccount.setCellStyle(headerCellStyle);
                accountInfoAcount++;
            }

            // Get the fees in pivot_fee_details
            PivotFeeDetailDTO pivotFeeDetailDTO = new PivotFeeDetailDTO();
            pivotFeeDetailDTO.setFeeType(FeeTypeEnum.MGT_FEE);
            pivotFeeDetailDTO.setOperateDate(endDate);
            RpcMessage<BigDecimal> sumMgtFee = pivotFeeDetailRemoteService.getTotalMoneyByDateAndFeeType(pivotFeeDetailDTO);
            if (sumMgtFee.isSuccess()) {
                totalMgtFee = sumMgtFee.getContent().doubleValue();
            }

            pivotFeeDetailDTO.setFeeType(FeeTypeEnum.MGT_GST);
            pivotFeeDetailDTO.setOperateDate(endDate);
            RpcMessage<BigDecimal> sumGstMgtFee = pivotFeeDetailRemoteService.getTotalMoneyByDateAndFeeType(pivotFeeDetailDTO);
            if (sumGstMgtFee.isSuccess()) {
                totalMgtGstFee = sumGstMgtFee.getContent().doubleValue();;
            }

            pivotFeeDetailDTO.setFeeType(FeeTypeEnum.CUST_FEE);
            pivotFeeDetailDTO.setOperateDate(endDate);
            RpcMessage<BigDecimal> sumCustodianFee = pivotFeeDetailRemoteService.getTotalMoneyByDateAndFeeType(pivotFeeDetailDTO);
            if (sumCustodianFee.isSuccess()) {
                totalCustodianFee = sumCustodianFee.getContent().doubleValue();;
            }

            // Management Fee total Cash
            Cell mgtCashAccount = cashRow.createCell(mgtColumnPos);
            mgtCashAccount.setCellValue(totalMgtFee);
            mgtCashAccount.setCellType(0); // Set as Numeric
            mgtCashAccount.setCellStyle(headerCellStyle);

            // Management GST Fee total Cash
            Cell mgtGstCashAccount = cashRow.createCell(mgtGstColumnPos);
            mgtGstCashAccount.setCellValue(totalMgtGstFee);
            mgtGstCashAccount.setCellType(0); // Set as Numeric
            mgtGstCashAccount.setCellStyle(headerCellStyle);

            // Custodian Fee total Cash
            Cell custodianCashAccount = cashRow.createCell(custodianColumnPos);
            custodianCashAccount.setCellValue(totalCustodianFee);
            custodianCashAccount.setCellType(0); // Set as Numeric
            custodianCashAccount.setCellStyle(headerCellStyle);

            // PFT totalCash
            Cell pftCashAccount = cashRow.createCell(pftColumnPos);
            pftCashAccount.setCellValue(pftCash);
            pftCashAccount.setCellType(0); // Set as Numeric
            pftCashAccount.setCellStyle(headerCellStyle);
            // End Cash Part;

            // The total Cash Sum (All account + MGT + GST + Custodian + PFT Cash)
            Cell totalCashCell = cashRow.createCell(3);
            totalCashCell.setCellValue(totalCash + totalMgtFee + totalMgtGstFee + totalCustodianFee + pftCash);
            totalCashCell.setCellType(0); // Set as Numeric
            totalCashCell.setCellStyle(headerCellStyle);

            Row assetRow = sheet.createRow(cashColumnStaringRow++);
            Cell assetLabelCell = assetRow.createCell(0);
            assetLabelCell.setCellValue("Asset");
            assetLabelCell.setCellType(1); // Set as String
            assetLabelCell.setCellStyle(headerCellStyle);

            Row mgtRow = sheet.createRow(cashColumnStaringRow++);
            Cell mgtLabelCell = mgtRow.createCell(0);
            mgtLabelCell.setCellValue("MGT");
            mgtLabelCell.setCellType(1); // Set as String
            mgtLabelCell.setCellStyle(headerCellStyle);

            Row gstRow = sheet.createRow(cashColumnStaringRow++);
            Cell gstLabelCell = gstRow.createCell(0);
            gstLabelCell.setCellValue("GST");
            gstLabelCell.setCellType(1); // Set as String
            gstLabelCell.setCellStyle(headerCellStyle);

            Row custodianRow = sheet.createRow(cashColumnStaringRow++);
            Cell custodianLabelCell = custodianRow.createCell(0);
            custodianLabelCell.setCellValue("CUSTODIAN");
            custodianLabelCell.setCellType(1); // Set as String
            custodianLabelCell.setCellStyle(headerCellStyle);

            Row adjCashRow = sheet.createRow(cashColumnStaringRow++);
            Cell adjCashLabelCell = adjCashRow.createCell(0);
            adjCashLabelCell.setCellValue("ADJ cash");
            adjCashLabelCell.setCellType(1); // Set as String
            adjCashLabelCell.setCellStyle(headerCellStyle);

            Row adjAssetRow = sheet.createRow(cashColumnStaringRow++);
            Cell adjAssetLabelCell = adjAssetRow.createCell(0);
            adjAssetLabelCell.setCellValue("ADJ Asset");
            adjAssetLabelCell.setCellType(1); // Set as String
            adjAssetLabelCell.setCellStyle(headerCellStyle);

            accountInfoAcount = 4; // reset to column 4;

            Double sumMgtFees = 0.00;
            Double sumMgtGstFees = 0.00;
            Double sumCustodianFees = 0.00;

            for (Map.Entry<String, AccountInfoResDTO> accountEntry : accountInfoMap.entrySet()) {
                AccountStaticsReqDTO accountStaticsReqDTO = new AccountStaticsReqDTO();
                accountStaticsReqDTO.setAccountId(Long.valueOf(accountEntry.getKey()));
                accountStaticsReqDTO.setStaticDate(endDate);
                RpcMessage<AccountStaticsResDTO> rpcMessageAccountStatics = accountStaticsRemoteService.selectByStaticDate(accountStaticsReqDTO);
                if (rpcMessageAccountStatics.isSuccess()) {
                    Double totalCashFlow = 0.00;
                    AccountStaticsResDTO accountStaticsResDTO = rpcMessageAccountStatics.getContent();

                    Cell assetCell = assetRow.createCell(accountInfoAcount);
                    assetCell.setCellValue(accountStaticsResDTO.getAdjFundAsset().doubleValue() + (accountStaticsResDTO.getMgtFee().doubleValue()
                            + accountStaticsResDTO.getGstMgtFee().doubleValue() + accountStaticsResDTO.getCustFee().doubleValue()));
                    assetCell.setCellType(0); // Set as Numeric
                    assetCell.setCellStyle(headerCellStyle);

                    Cell mgtCell = mgtRow.createCell(accountInfoAcount);
                    mgtCell.setCellValue(accountStaticsResDTO.getMgtFee().doubleValue());
                    mgtCell.setCellType(0); // Set as Numeric
                    mgtCell.setCellStyle(headerCellStyle);
                    sumMgtFees += accountStaticsResDTO.getMgtFee().doubleValue();

                    Cell mgtGstCell = gstRow.createCell(accountInfoAcount);
                    mgtGstCell.setCellValue(accountStaticsResDTO.getGstMgtFee().doubleValue());
                    mgtGstCell.setCellType(0); // Set as Numeric
                    mgtGstCell.setCellStyle(headerCellStyle);
                    sumMgtGstFees += accountStaticsResDTO.getGstMgtFee().doubleValue();

                    Cell custodianCell = custodianRow.createCell(accountInfoAcount);
                    custodianCell.setCellValue(accountStaticsResDTO.getCustFee().doubleValue());
                    custodianCell.setCellType(0); // Set as Numeric
                    custodianCell.setCellStyle(headerCellStyle);
                    sumCustodianFees += accountStaticsResDTO.getCustFee().doubleValue();

                    Cell adjCashCell = adjCashRow.createCell(accountInfoAcount);
                    adjCashCell.setCellValue(accountStaticsResDTO.getAdjCashHolding().doubleValue());
                    adjCashCell.setCellType(0); // Set as Numeric
                    adjCashCell.setCellStyle(headerCellStyle);

                    Cell adjAssetCell = adjAssetRow.createCell(accountInfoAcount);
                    // FundAsset - (Sum all the fees)
                    Double adjFundAsset = accountStaticsResDTO.getAdjFundAsset().doubleValue();

                    adjAssetCell.setCellValue(adjFundAsset);
                    adjAssetCell.setCellType(0); // Set as Numeric
                    adjAssetCell.setCellStyle(headerCellStyle);

                    String totalCashKey = "" + accountEntry.getKey() + ":total:cash:";
                    // Set into total row later
                    totalCashFlow += adjFundAsset;
                    redissonHelper.set(totalCashKey, totalCashFlow);
                }
                accountInfoAcount++;
            }

            Cell adjMgtCashCell = adjCashRow.createCell(mgtColumnPos);
            Double adjMgtCash = totalMgtFee - sumMgtFees;
            adjMgtCashCell.setCellValue(adjMgtCash);
            adjMgtCashCell.setCellType(0); // Set as Numeric
            adjMgtCashCell.setCellStyle(headerCellStyle);

            Cell adjMgtGstCashCell = adjCashRow.createCell(mgtGstColumnPos);
            Double adjMgtGstCash = totalMgtGstFee - sumMgtGstFees;
            adjMgtGstCashCell.setCellValue(adjMgtGstCash);
            adjMgtGstCashCell.setCellType(0); // Set as Numeric
            adjMgtGstCashCell.setCellStyle(headerCellStyle);

            Cell adjCustodianCashCell = adjCashRow.createCell(custodianColumnPos);
            Double adjCustodianCash = totalCustodianFee - sumCustodianFees;
            adjCustodianCashCell.setCellValue(adjCustodianCash);
            adjCustodianCashCell.setCellType(0); // Set as Numeric
            adjCustodianCashCell.setCellStyle(headerCellStyle);

            // PFT total Cash
            Cell adjPftCashCell = adjCashRow.createCell(pftColumnPos);
            adjPftCashCell.setCellValue(pftCash);
            adjPftCashCell.setCellType(0); // Set as Numeric
            adjPftCashCell.setCellStyle(headerCellStyle);
            // End Total Cash Part

            // PFT totalAsset
            Cell pftTotalAsset = adjAssetRow.createCell(pftColumnPos);
            pftTotalAsset.setCellValue(totalPftAssetValue);
            pftTotalAsset.setCellType(0); // Set as Numeric
            pftTotalAsset.setCellStyle(headerCellStyle);
            // End PFT totalAsset Part;                      

            Row totalRow = sheet.createRow(cashColumnStaringRow++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("Total");
            totalLabelCell.setCellType(1); // Set as String
            totalLabelCell.setCellStyle(headerCellStyle);

            accountInfoAcount = 4; // reset to column 4;

            // Total $ per account      
            Double finalSumAllMoney = 0.00;
            for (Map.Entry<String, AccountInfoResDTO> accountEntry : accountInfoMap.entrySet()) {
                AccountInfoResDTO accountInfoResDTO = accountEntry.getValue();
                String totalCashKey = "" + accountInfoResDTO.getId() + ":total:cash:";
                Cell _totalCashCell = totalRow.createCell(accountInfoAcount);
                if (redissonHelper.exists(totalCashKey)) {
                    Double _totalCash = (Double) redissonHelper.get(totalCashKey);
                    _totalCashCell.setCellValue(_totalCash);
                    finalSumAllMoney += _totalCash;
                    redissonHelper.del(totalCashKey);
                } else {
                    _totalCashCell.setCellValue(0.00);
                }
                _totalCashCell.setCellType(0); // Set as numeric
                _totalCashCell.setCellStyle(headerCellStyle);
                accountInfoAcount++;
            }

            Cell finalMgtCashCell = totalRow.createCell(mgtColumnPos);
            finalMgtCashCell.setCellValue(adjMgtCash);
            finalMgtCashCell.setCellType(0); // Set as Numeric
            finalMgtCashCell.setCellStyle(headerCellStyle);

            Cell finalMgtGstCashCell = totalRow.createCell(mgtGstColumnPos);
            finalMgtGstCashCell.setCellValue(adjMgtGstCash);
            finalMgtGstCashCell.setCellType(0); // Set as Numeric
            finalMgtGstCashCell.setCellStyle(headerCellStyle);

            Cell finalCustodianCashCell = totalRow.createCell(custodianColumnPos);
            finalCustodianCashCell.setCellValue(adjCustodianCash);
            finalCustodianCashCell.setCellType(0); // Set as Numeric
            finalCustodianCashCell.setCellStyle(headerCellStyle);

            Cell finalPftCashCell = totalRow.createCell(pftColumnPos);
            finalPftCashCell.setCellValue(totalPftAssetValue + pftCash);
            finalPftCashCell.setCellType(0); // Set as Numeric
            finalPftCashCell.setCellStyle(headerCellStyle);

            Cell totalAssetCell = totalRow.createCell(3);
            totalAssetCell.setCellValue(finalSumAllMoney + adjMgtCash + adjMgtGstCash + adjCustodianCash + totalPftAssetValue + pftCash);
            totalAssetCell.setCellType(0); // Set as numeric
            totalAssetCell.setCellStyle(headerCellStyle);

            for (int i = 0; i <= pftColumnPos; i++) {
                sheet.autoSizeColumn(i);
            }

            CellStyle dateCellStyle = workbook.createCellStyle();

            if (PropertiesUtil.getString("env.remark").equalsIgnoreCase("local")) {
                filePath = "D:/" + fileName;
            } else {
                if (!new File("/data/reports/").exists()) {
                    new File("/data/reports/").mkdirs();
                }
                filePath = "/data/reports/" + fileName;
            }
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
                workbook.write(fileOut);
            }

        } catch (IOException | NumberFormatException e) {
            throw new BusinessException("Something wrong while generating the report");
        }

        File file = new File(filePath);
        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName + "\""));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    private Map<String, SaxoShareOpenPositionResDTO> getSaxoShareOpenPosition(Date startDate, Date endDate) {
        Map<String, SaxoShareOpenPositionResDTO> sortMapProduct = new TreeMap<>();
        SaxoShareOpenPositionReqDTO saxoShareOpenPositionReqDTO = new SaxoShareOpenPositionReqDTO();
        saxoShareOpenPositionReqDTO.setPageNo(1);
        saxoShareOpenPositionReqDTO.setPageSize(1000);
        saxoShareOpenPositionReqDTO.setStartCreateTime(startDate);
        saxoShareOpenPositionReqDTO.setEndCreateTime(endDate);

        RpcMessage<List<SaxoShareOpenPositionResDTO>> rpcSaxoShareOpenPositionRes
                = saxoStatisticRemoteService.saxoShareOpenPositionRemoteExport(saxoShareOpenPositionReqDTO);

        if (rpcSaxoShareOpenPositionRes.isSuccess()) {
            List<SaxoShareOpenPositionResDTO> saxoShareOpenPositionResDTOList = rpcSaxoShareOpenPositionRes.getContent();
            saxoShareOpenPositionResDTOList.forEach((saxoShareOpenPositionResDTO) -> {
                sortMapProduct.put(saxoShareOpenPositionResDTO.getProductCode(), saxoShareOpenPositionResDTO);
            });
        }
        return sortMapProduct;
    }

    private Map<String, AccountInfoResDTO> getAccountInfo() {
        Map<String, AccountInfoResDTO> sortMapAccountInfo = new TreeMap<>();
        AccountInfoReqDTO accountInfoReqDTO = new AccountInfoReqDTO();
        accountInfoReqDTO.setPageNo(1);
        accountInfoReqDTO.setPageSize(1000);
        RpcMessage<Page<AccountInfoResDTO>> accountInfoPageRpc = accountInfoRemoteService.getAccountInfoPage(accountInfoReqDTO);
        if (accountInfoPageRpc.isSuccess()) {
            Page<AccountInfoResDTO> accountInfoPage = accountInfoPageRpc.getContent();
            List<AccountInfoResDTO> accountInfoList = accountInfoPage.getRecords();
            accountInfoList.forEach((accountInfoResDTO) -> {
                sortMapAccountInfo.put("" + accountInfoResDTO.getId(), accountInfoResDTO);
            });
        }
        return sortMapAccountInfo;
    }

    private Map<String, AccountetfSharesResDTO> getAccountEtfShares(Long accountId, Date endDate) {
        Map<String, AccountetfSharesResDTO> sortMapAccountAsset = new TreeMap<>();
        AccountetfSharesReqDTO accountetfSharesReqDTO = new AccountetfSharesReqDTO();
        accountetfSharesReqDTO.setAccountId(accountId);
        accountetfSharesReqDTO.setStaticDate(endDate);
        RpcMessage<List<AccountetfSharesResDTO>> accountEtfSharesRpc = accountEtfSharesRemoteService.selectByStaticDate(accountetfSharesReqDTO);

        if (accountEtfSharesRpc.isSuccess()) {
            List<AccountetfSharesResDTO> accountetfSharesResDTOList = accountEtfSharesRpc.getContent();
            for (AccountetfSharesResDTO accountetfSharesResDTO : accountetfSharesResDTOList) {
                AccountEtfResVo accountEtfResVo = new AccountEtfResVo();
                accountEtfResVo.setAmount(accountetfSharesResDTO.getMoney());
                accountEtfResVo.setProductCode(accountetfSharesResDTO.getProductCode());
                accountEtfResVo.setShare(accountetfSharesResDTO.getShares());
                CalDecimal<AccountEtfResVo> calDecimal = new CalDecimal<>();
                calDecimal.handleDot(accountEtfResVo);
                sortMapAccountAsset.put(accountetfSharesResDTO.getProductCode(), accountetfSharesResDTO);
            }
        }
        return sortMapAccountAsset;
    }

    private Map<String, PivotPftAssetResDTO> getPftAccountAsset(Date date) {
        Map<String, PivotPftAssetResDTO> sortMapAccountAsset = new TreeMap<>();
        RpcMessage<List<PivotPftAssetResDTO>> pivotPftAssetRpc = pivotPftRemoteService.getPftAssets(date);
        log.info("getPftAccountAsset, pivotPftAssetRpc {} ", pivotPftAssetRpc);
        if (pivotPftAssetRpc.isSuccess()) {
            List<PivotPftAssetResDTO> pivotPftAssetResList = pivotPftAssetRpc.getContent();
            for (PivotPftAssetResDTO pivotPftAsset : pivotPftAssetResList) {
                sortMapAccountAsset.put(pivotPftAsset.getProductCode(), pivotPftAsset);
            }

        }
        return sortMapAccountAsset;
    }
}
