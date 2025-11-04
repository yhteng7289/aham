package com.pivot.aham.api.service.job.custstatment.impl;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.pivot.aham.api.server.dto.UserGoalInfoDTO;
import com.pivot.aham.api.server.dto.UserGoalInfoResDTO;
import com.pivot.aham.api.server.dto.UserInfoResDTO;
import com.pivot.aham.api.server.remoteservice.UserServiceRemoteService;
import com.pivot.aham.api.service.job.custstatment.CustomerStatementJob;
import com.pivot.aham.api.service.mapper.model.AccountSummaryPO;
import com.pivot.aham.api.service.mapper.model.AccountUserPO;
import com.pivot.aham.api.service.mapper.model.AnnexPO;
import com.pivot.aham.api.service.mapper.model.AssetHoldingPO;
import com.pivot.aham.api.service.mapper.model.FeeAndChargesPO;
import com.pivot.aham.api.service.mapper.model.GlossaryPO;
import com.pivot.aham.api.service.mapper.model.UserCustStatementPO;
import com.pivot.aham.api.service.service.AccountUserService;
import com.pivot.aham.api.service.service.UserCustStatementService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.elasticjob.ElasticJobConf;
import com.pivot.aham.common.core.support.context.ApplicationContextHolder;
import com.pivot.aham.common.core.util.AwsUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.InstanceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 重置account过程数据统计状态
 *
 * @author addison
 * @since 2018年12月06日
 */
@ElasticJobConf(name = "CustomerStatementJob_2",
        cron = "0 0 22 2 * ?",
        shardingItemParameters = "0=1",
        shardingTotalCount=1,
        description = "生成用户月报")
@Slf4j
public class CustomerStatementJobImpl implements SimpleJob, CustomerStatementJob {
    @Resource
    private AccountUserService accountUserService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;
    @Resource
    private GenAnnex genAnnex;
    @Resource
    private GenAccountSummary genAccountSummary;
    @Resource
    private GenGlossary genGlossary;
    @Resource
    private GenAssetHolding genAssetHolding;
    @Resource
    private GenCashActivity genCashActivity;
    @Resource
    private GenFeeAndCharges genFeeAndCharges;
    @Resource
    private UserCustStatementService userCustStatementService;


    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("CustomerStatementJob_2=========>开始");
        try {
            //calculateCustomerStatement(null, null);
        }catch (Exception e){
            ErrorLogAndMailUtil.logError(log,e);
        }
        log.info("CustomerStatementJob_2=========>结束");
    }

    @Override
    public void calculateCustomerStatement(String clientId, Integer monthOffset) {
        if(monthOffset == null){
            monthOffset= -1;
        }
        Date lastMonth = DateUtils.addMonths(new Date(), monthOffset);
        Date startTime = DateUtils.monthStart(lastMonth);
        Date endTime = DateUtils.monthEnd(lastMonth);
        //获取所有用户
        List<UserInfoResDTO> userInfoResDTOS = userServiceRemoteService.queryUserList();
        for (UserInfoResDTO userInfoResDTO : userInfoResDTOS) {
            try {
                if (StringUtils.isNotEmpty(clientId) && !userInfoResDTO.getClientId().equals(clientId)) {
                    continue;
                }

                CustomerStatementVo customerStatementVo = new CustomerStatementVo();
                customerStatementVo.setAddress(userInfoResDTO.getAddress());
                customerStatementVo.setMobileNum(userInfoResDTO.getMobileNum());
                customerStatementVo.setStartTime(DateUtils.formatDate(startTime,"yyyy-MM-dd"));
                customerStatementVo.setEndTime(DateUtils.formatDate(endTime,"yyyy-MM-dd"));
                customerStatementVo.setClientId(userInfoResDTO.getClientId());
                customerStatementVo.setClientName(userInfoResDTO.getClientName());
                customerStatementVo.setCustStatemnetStaticDate(DateUtils.formatDate(new Date(),"yyyy-MM-dd"));
                //annex
                List<AnnexReportBean> annexReportBeanList = Lists.newArrayList();
                //获取所有用户的goal
                AccountUserPO accountUserQuery = new AccountUserPO();
                accountUserQuery.setClientId(userInfoResDTO.getClientId());
                List<AccountUserPO> accountUserPOList = accountUserService.queryList(accountUserQuery);
                //每个goal的annex
                for (AccountUserPO accountUser : accountUserPOList) {
                    List<AnnexPO> annexPOList = genAnnex.genAnnexReport(accountUser,startTime,endTime);
                    if (annexPOList.size() != 0) {
                        AnnexReportBean annexReportBean = new AnnexReportBean();
                        annexReportBean.setAccountId(accountUser.getAccountId());
                        annexReportBean.setClientId(accountUser.getClientId());
                        annexReportBean.setGoalId(accountUser.getGoalId());
                        //查询goalName
                        UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
                        userGoalInfoDTO.setClientId(accountUser.getClientId());
                        userGoalInfoDTO.setGoalId(accountUser.getGoalId());
                        RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
                        if (userGoalInfoRes.isSuccess()) {
                            UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                            annexReportBean.setGoalName(userGoal.getGoalName());
                        } else {
                            annexReportBean.setGoalName(accountUser.getGoalId());
                        }


                        annexReportBean.setAnnexPOList(annexPOList);

                        annexReportBeanList.add(annexReportBean);
                    }
                }
                if (annexReportBeanList.size() > 0) {
                    customerStatementVo.setAnnexReportBeanList(annexReportBeanList);
                }

                //summary
                AccountUserPO accountUserForSummary = new AccountUserPO();
                accountUserForSummary.setClientId(userInfoResDTO.getClientId());
                AccountSummaryPO accountSummaryPO = genAccountSummary.genAccountSummary(accountUserForSummary,startTime,endTime);
                if (accountSummaryPO != null) {
                    customerStatementVo.setAccountSummaryPO(accountSummaryPO);
                }

                //glossary
                AccountUserPO accountUserForGlossary = new AccountUserPO();
                accountUserForGlossary.setClientId(userInfoResDTO.getClientId());
                List<GlossaryPO> glossaryPOList = genGlossary.genGlossary(accountUserForGlossary,startTime,endTime);
                if (glossaryPOList != null && glossaryPOList.size() > 0) {
                    customerStatementVo.setGlossaryPOList(glossaryPOList);
                }

                //asset holding
                List<AssetHoldingReportBean> assetHoldingReportBeanList = Lists.newArrayList();
                for (AccountUserPO accountUser : accountUserPOList) {
                    AssetHoldingReportBean assetHoldingReportBean = new AssetHoldingReportBean();
                    List<AssetHoldingPO> assetHoldingPOList = genAssetHolding.genAssetHolding(accountUser,startTime,endTime);
                    if (CollectionUtils.isEmpty(assetHoldingPOList)) {
                        continue;
                    }
                    assetHoldingReportBean.setAccountId(accountUser.getAccountId());
                    assetHoldingReportBean.setClientId(accountUser.getClientId());
                    assetHoldingReportBean.setGoalId(accountUser.getGoalId());
                    //查询goalName
                    UserGoalInfoDTO userGoalInfoDTO = new UserGoalInfoDTO();
                    userGoalInfoDTO.setClientId(accountUser.getClientId());
                    userGoalInfoDTO.setGoalId(accountUser.getGoalId());
                    RpcMessage<UserGoalInfoResDTO> userGoalInfoRes = userServiceRemoteService.getUserGoalInfo(userGoalInfoDTO);
                    if(userGoalInfoRes.isSuccess()) {
                        UserGoalInfoResDTO userGoal = userGoalInfoRes.getContent();
                        assetHoldingReportBean.setGoalName(userGoal.getGoalName());
                    }else{
                        assetHoldingReportBean.setGoalName(accountUser.getGoalId());
                    }



                    assetHoldingReportBean.setAssetHoldingPOList(assetHoldingPOList);

                    BigDecimal totalCloseValue = BigDecimal.ZERO;
                    BigDecimal totalDividendRecive = BigDecimal.ZERO;
                    BigDecimal totalOpenPrecnet = BigDecimal.ZERO;
                    BigDecimal totalOpenValue = BigDecimal.ZERO;
                    for (AssetHoldingPO assetHoldingPO : assetHoldingPOList) {
                        totalCloseValue = totalCloseValue.add(assetHoldingPO.getCloseValue());
                        totalDividendRecive = totalDividendRecive.add(assetHoldingPO.getDividendRecive());
                        totalOpenPrecnet = totalOpenPrecnet.add(assetHoldingPO.getOpenPrecnet());
                        totalOpenValue = totalOpenValue.add(assetHoldingPO.getOpenValue());
                    }

                    assetHoldingReportBean.setTotalCloseValue(totalCloseValue);
                    assetHoldingReportBean.setTotalDividendRecive(totalDividendRecive);
                    assetHoldingReportBean.setTotalOpenPrecnet(totalOpenPrecnet);
                    assetHoldingReportBean.setTotalOpenValue(totalOpenValue);

                    assetHoldingReportBeanList.add(assetHoldingReportBean);
                }
                if (assetHoldingReportBeanList.size() > 0) {
                    customerStatementVo.setAssetHoldingReportBeanList(assetHoldingReportBeanList);
                }

                //cash activity
                AccountUserPO accountUserForCashActivity = new AccountUserPO();
                accountUserForCashActivity.setClientId(userInfoResDTO.getClientId());
                CashActivityBean cashActivityBean = genCashActivity.genCashActivity(accountUserForCashActivity,startTime,endTime);
                if (cashActivityBean.getCashActivityForSquirrelSaveBean() != null || cashActivityBean.getCashActivityForGoalBeanList().size() > 0) {
                    customerStatementVo.setCashActivityBean(cashActivityBean);
                }

                //fee and charges
                FeeAndChargesBean feeAndChargesBean = new FeeAndChargesBean();
                feeAndChargesBean.setClientId(userInfoResDTO.getClientId());
                AccountUserPO accountUserForFee = new AccountUserPO();
                accountUserForFee.setClientId(userInfoResDTO.getClientId());
                List<FeeAndChargesPO> feeAndChargesPOS = genFeeAndCharges.genFeeAndCharges(accountUserForFee,startTime,endTime);
                if (feeAndChargesPOS.size() > 0) {
                    BigDecimal totalMgtFee = BigDecimal.ZERO;
                    BigDecimal totalCustFee = BigDecimal.ZERO;
                    BigDecimal subTotal = BigDecimal.ZERO;
                    BigDecimal totalGst = BigDecimal.ZERO;
                    BigDecimal total = BigDecimal.ZERO;
                    for (FeeAndChargesPO feeAndCharges : feeAndChargesPOS) {
                        totalMgtFee = totalMgtFee.add(feeAndCharges.getMgtFeeSgd());
                        totalCustFee = totalCustFee.add(feeAndCharges.getCustFeeSgd());
                        subTotal = subTotal.add(feeAndCharges.getMgtFeeSgd()).add(feeAndCharges.getCustFeeSgd());
                        totalGst = totalGst.add(feeAndCharges.getGstMgtFeeSgd());
                        total = total.add(feeAndCharges.getMgtFeeSgd()).add(feeAndCharges.getCustFeeSgd()).add(feeAndCharges.getGstMgtFeeSgd());

                    }
                    feeAndChargesBean.setTotal(total);
                    feeAndChargesBean.setSubTotal(subTotal);
                    feeAndChargesBean.setTotalCustFee(totalCustFee);
                    feeAndChargesBean.setTotalMgtFee(totalMgtFee);
                    feeAndChargesBean.setTotalGst(totalGst);
                }
                feeAndChargesBean.setFeeAndChargesList(feeAndChargesPOS);
                customerStatementVo.setFeeAndChargesBean(feeAndChargesBean);

                //生成报表
                if(customerStatementVo.getAccountSummaryPO() != null) {
                    log.error("生成用户月报{}", userInfoResDTO.getClientId());
                    String pdfUrl = genPdf(customerStatementVo, userInfoResDTO.getClientId());


                    //保存url
                    UserCustStatementPO userCustStatementQuery = new UserCustStatementPO();
                    userCustStatementQuery.setClientId(userInfoResDTO.getClientId());
                    userCustStatementQuery.setStaticDate(new Date());
                    UserCustStatementPO userCustStatementOld = userCustStatementService.selectByStaticDate(userCustStatementQuery);

                    UserCustStatementPO userCustStatementPO = new UserCustStatementPO();
                    userCustStatementPO.setClientId(userInfoResDTO.getClientId());
                    userCustStatementPO.setPdfUrl(pdfUrl);
                    userCustStatementPO.setStaticDate(new Date());
                    if (userCustStatementOld != null) {
                        userCustStatementPO.setId(userCustStatementOld.getId());
                    }
                    userCustStatementService.updateOrInsert(userCustStatementPO);
                }

            } catch (Exception e) {
                log.error("用户{},生成月报出错", userInfoResDTO.getClientId(), e);
                ErrorLogAndMailUtil.logError(log, e);
            }
        }
    }

    /**
     * 生成报表
     * @param customerStatementVo
     */
    private String genPdf(CustomerStatementVo customerStatementVo,String clientId) throws IOException {
        PdfWriter pdfWriter = null;
        OutputStream outputStream = new ByteArrayOutputStream();

        ConverterProperties converterProperties = new ConverterProperties();
        String monthStr = DateUtils.formatDate(new Date(),"yyyy-MM");
        String fileName = clientId+"_custstatement_"+monthStr+".pdf";
//        String ftpPath = PropertiesUtil.getString("ftp.pivot.custstatement") + "/statement/" + fileName;
//        outputStream = FTPClientUtil.getFtpOutPutStream(ftpPath);
//        File tmpFile = new File(fileName);
//        tmpFile.createNewFile();
        pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        TemplateEngine templateEngine = (TemplateEngine) ApplicationContextHolder.getBean("templateEngine");
        Context context = new Context();
        context.setVariables(InstanceUtil.newHashMap("customerStatementVo",customerStatementVo));
        String body = templateEngine.process("CustomerStatement",context);
//            HtmlConverter.convertToPdf(body,pdfWriter);
        Document document = HtmlConverter.convertToDocument(body, pdfDocument, converterProperties);
        document.close();
        InputStream inputStream = parse(outputStream);
        String s3Path = "statement/"+fileName;
        AwsUtil.uploadFile("statement/"+fileName,inputStream);
        log.error("生成用户月报成功{},文件名:{}",clientId,fileName);
//            tmpFile.delete();

        return s3Path;
    }
    private ByteArrayInputStream parse(final OutputStream out) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        final ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }


}
