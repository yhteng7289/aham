package com.pivot.aham.api.web.web.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.amazonaws.services.s3.model.S3Object;
import com.pivot.aham.api.server.dto.SaxoToUobOfflineConfirmByExcelDTO;
import com.pivot.aham.api.server.dto.req.ReCalBuyEtfInBalReqDTO;
import com.pivot.aham.api.server.remoteservice.*;
import com.pivot.aham.api.web.web.vo.ModelRecommendReqVo;
import com.pivot.aham.api.web.core.ExceptionUtil;
import com.pivot.aham.common.core.base.AbstractController;
import com.pivot.aham.common.core.base.Message;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.support.cache.RedissonHelper;
import com.pivot.aham.common.core.support.file.excel.ImportExcel;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.AwsUtil;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import com.pivot.aham.common.core.util.PropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Preconditions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by luyang.li on 18/12/9.
 * <p>
 * 提供给FE的用户资产接口
 */
@RestController
@RequestMapping("/testTool/")
@Api(value = "测试使用工具", description = "测试使用工具")
@Slf4j
public class TestToolController extends AbstractController {
    ExecutorService executorService = new ThreadPoolExecutor(2, 20, 5, TimeUnit.SECONDS,
            new ArrayBlockingQueue(20), new ThreadPoolExecutor.DiscardOldestPolicy());

    @Resource
    private RechargeServiceRemoteService rechargeServiceRemoteService;
    @Resource
    private ModelServiceRemoteService modelServiceRemoteService;
    @Resource
    private AssetServiceRemoteService assetServiceRemoteService;
    @Resource
    private TestRemoteService testRemoteService;
    @Resource
    private TestExcuteRemoteService testExcuteRemoteService;
    @Resource
    private WithdrawalRemoteService withdrawalRemoteService;
    @Resource
    private DividendRemoteService dividendRemoteService;
    @Resource
    private AccountReBalanceRemoteService accountReBalanceRemoteService;
    @Autowired
    private RedissonHelper redissonHelper;
    @Resource
    private PivotErrorDetailRemoteService pivotErrorDetailRemoteService;
    @Resource
    private SaxoTradeRemoteService saxoTradeRemoteService;
    @Resource
    private UserServiceRemoteService userServiceRemoteService;


    @Resource
    private SaxoStatisticRemoteService saxoStatisticRemoteService;

    @Resource
    private RabbitTemplate rabbitTemplate;


    /**
     * mq测试
     *
     */
    @RequestMapping("mqTest.api")
    public Message mqTest(String exchange,String routeKey){
        //再发送mq
        rabbitTemplate.convertAndSend(exchange, routeKey,"test", message -> {
            message.getMessageProperties().setMessageId("test123");
            return message;
        },new CorrelationData("test123"));
        log.info("退款订单,mq发送完毕");

        return Message.success();
    }

    /**
     * qps并发测试
     * @return
     */
    @RequestMapping("apsTest.api")
    @SentinelResource(value = "qpsFlow", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public Message apsTest() throws InterruptedException {
        Thread.sleep(1000);
        return Message.success();
    }

    /**
     * 测试异常监控
     */
    @RequestMapping("testError.api")
    public void testError()  {
        throw new RuntimeException("test");
    }

    @RequestMapping("custStatement.api")
    public Message custStatement(String clientId,Integer monthOffset)  {
        testRemoteService.calCustStatement(clientId,monthOffset);
        return Message.success();
    }

    /**
     * 模拟从 UOB 转账进pivot
     * <p>
     * * @return
     */
    @RequestMapping("virtualAccountOfflineTransfer.api")
    @ApiOperation(value = "to", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message vAOffineTransfer() {
        userServiceRemoteService.UobRechargeSyncJobImpl();
        return Message.success();
    }

    /**
     * 触发模型同步任务
     *
     * @param modelRecommendReqVo
     * @return
     * @throws ParseException
     */
    @RequestMapping("trigger/model/recommend.api")
    public Message triggerRecommendJob(@RequestBody ModelRecommendReqVo modelRecommendReqVo) throws ParseException {
        log.info("模型信息同步,请求参数,date:{}", JSON.toJSON(modelRecommendReqVo));
        modelServiceRemoteService.triggerRecommendJob(modelRecommendReqVo.getRequiredDate());
        log.info("模型信息同步,完成");
        return Message.success();
    }


    @RequestMapping("trigger/model/ahamRecommend.api")
    public Message triggerRecommendJob() throws ParseException {
//        log.info("模型信息同步,请求参数,date:{}", JSON.toJSON(modelRecommendReqVo));
        modelServiceRemoteService.triggerAhamRecommendJob();
//        log.info("模型信息同步,完成");
        return Message.success();
    }

    /**
     * 触发历史模型同步任务
     *
     * @return
     * @throws ParseException
     */
    @RequestMapping("trigger/hisModel/recommend.api")
    public Message triggerRecommendJob(String date, int days) {
        log.info("模型信息同步,请求参数,date:{}", date);
        modelServiceRemoteService.triggerHisRecommendJob(date, days);
        log.info("模型信息同步,完成");
        return Message.success();
    }

    /**
     * 触发收益曲线任务
     *
     * @return
     */
    @RequestMapping("trigger/model/portLevl.api")
    public Message triggerPortlevel() {
        log.info("收益曲线,请求参数");
        modelServiceRemoteService.triggerPortlevel();
        return Message.success();
    }

    /**
     * UOB转账回调
     *
     * @return
     */
    @RequestMapping("/uboTransferSaxoCallback.api")
    public Message uboTransferSaxoCallback() {
        log.info("资产从UOB转账进入SAXO回调开始");
        rechargeServiceRemoteService.uboTransferSaxoCallback();
        log.info("资产从UOB转账进入SAXO回调结束");
        return Message.success();
    }

    /**
     * 交易分析购买ETF
     *
     * @return
     */
    @RequestMapping("/tradeAnalysisJob.api")
    public Message tradeAnalysisJob(String accountId) {
        log.info("交易分析开始");
        rechargeServiceRemoteService.tradeAnalysisJob(accountId);
        log.info("交易分析结束");
        return Message.success();
    }

    /**
     * ETF 购买回调
     *
     * @param tmpOrderId
     * @return
     */
    @RequestMapping("/buyEtfCallback.api")
    public Message buyEtfCallback(Long tmpOrderId) {
        log.info("buyEtfCallback开始");
        assetServiceRemoteService.etfCallBackMock(tmpOrderId);
        log.info("buyEtfCallback开始");
        return Message.success();
    }

    /**
     * 账户自建基金净值计算
     *
     * @return
     */
    @RequestMapping("/assetsFundNav")
    public Message assetsFundNav(String date, Long accountId) {
        log.info("#######账户自建基金净值计算手动触发，date:{},accountId:{}#######", date, accountId);
        assetServiceRemoteService.assetsFundNav(date, accountId);
        log.info("#######账户自建基金净值计算手动完成，date:{},accountId:{}#######", date, accountId);
        return Message.success();
    }

//    /**
//     * 哦用户自建基金净值计算
//     *
//     * @return
//     */
//    @RequestMapping("/userAssetsFundNav")
//    public Message userAssetsFundNav() {
//        assetServiceRemoteService.userAssetsFundNav();
//        return Message.success();
//    }

    @RequestMapping("/test")
    public Message test() {
        log.info("#######99999999999999#######");
        Long executeOrderId = 0L;
        executeOrderId = Sequence.next();
        return Message.success(executeOrderId);
    }

    /**
     * 生产对应指令文件到ftp并下载到本地
     */
    @RequestMapping("/withdrawalSaxoToUob")
    public Message withdrawalSaxoToUob(HttpServletResponse response, HttpServletRequest request) {
        RpcMessage rpcMessage = testRemoteService.withdrawalSaxoToUob();
        return Message.success(rpcMessage);
    }

    public String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 确认saxo to uob转账
     * 读取指定文件并确认
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    @Validated
    @RequestMapping("/confirmSaxoToUob")
    public Message importSaxoToUobRecord(@NotBlank(message = "fialeName不能为空") String fileName) throws Exception {
        String filePath = PropertiesUtil.getString("ftp.pivot.confirm") + "/confirm/saxoOfflineConfirm/" + fileName;

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        if (inputStream == null) {
            Message.error("该文件不存在");
        }
        ImportExcel importExcel = new ImportExcel(fileName, inputStream, 0, 0);
        List<SaxoToUobOfflineConfirmByExcelDTO> lists = importExcel.getDataList(SaxoToUobOfflineConfirmByExcelDTO.class);
        for (SaxoToUobOfflineConfirmByExcelDTO saxoToUobOfflineConfirmByExcelDTO : lists) {
            withdrawalRemoteService.saxoToUobOfflineConfirmByExcel(saxoToUobOfflineConfirmByExcelDTO);
        }
        return Message.success("导入并处理成功");
    }

    /**
     * uobtobank转账
     */
    @RequestMapping("/withdrawalUobToBankTransfer")
    public void withdrawalUobToBankTransfer() {
        testRemoteService.withdrawalUobToBankTransfer();
    }

    /**
     *  UOB 美金内部给购汇回调
     *
     * @param vaOrderId
     */
    @RequestMapping("/uboExchangeCallback")
    public void uboExchangeCallback(Long vaOrderId) {
        testRemoteService.uboExchangeCallback(vaOrderId);
    }

    /**
     *  UOB 新币转  SAXO
     *
     */
    @RequestMapping("/uobTransferToSaxoJob")
    public void uobTransferToSaxoJob(){
        testRemoteService.uobTransferToSaxoJob();
    }


//    @RequestMapping("/dividend")
//    public Message dividend(@RequestBody DividendCallBackDTO dividendCallBackDTO) {
//        dividendRemoteService.dividendCallBack(dividendCallBackDTO);
//        return Message.success("分红模拟成功");
//    }

    @RequestMapping("/staticEtfShares")
    public Message staticEtfShares(Date date) {
        testRemoteService.staticAccountEtfJob(date);
        return Message.success("统计成功");
    }

    @RequestMapping("/staticUserEtfShares")
    public Message staticUserEtfShares(Date date) {
        testRemoteService.staticUserEtfJob(date);
        return Message.success("统计成功");
    }

    @RequestMapping("/finishNotify")
    public Message finishNotify() {

        try {
            testExcuteRemoteService.finishNotify();
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        return Message.success("通知成功");
    }

    public Message cleanUserData(Integer clientId, String date, String accountId, String goalId) {
        log.info("cleanUserData,request clientId:{}, date:{}, accountId:{}, goalId:{}", clientId, date, accountId, goalId);
//        testRemoteService.cleanUserData(clientId, date, accountId, goalId);
        return Message.success("cleanUserData, success");
    }

    @RequestMapping("/cleanRegister")
    public Message cleanUserDataRegister(Integer clientId) {
        log.info("cleanRegister,request clientId:{}", clientId);
        testRemoteService.cleanRegister(clientId);
        return Message.success("cleanUserData, success");
    }

    @RequestMapping("/redisTest")
    public Message redisTest(String name) {
        log.info("cleanRegister,request name:{}", name);

        redissonHelper.set("redisTest", "redisTest");
        String value = redissonHelper.get("redisTest");

        return Message.success("redis_value:" + value);
    }

    @RequestMapping("/accountReBalance")
    public Message redisTest(@RequestBody List<ReCalBuyEtfInBalReqDTO> reCalBuyEtfInBalReqDTOList) {
        accountReBalanceRemoteService.reCalBuyEtfInBal(reCalBuyEtfInBalReqDTOList);
        return Message.success();
    }


    //==========删除表中的记录
    @RequestMapping("/deleteFromTable")
    public Message deleteFromTable(String tableName, Long id) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(tableName), "表名必须存在");
        Preconditions.checkArgument(id > 0, "Id必须存在");
        log.info("fixRecharge开始,tableName:{},id:{}", tableName, id);
        testRemoteService.deleteFromTable(tableName, id);
        log.info("fixRecharge结束,tableName:{},id:{}", tableName, id);
        return Message.success();
    }

    @RequestMapping("/updateClientName")
    public Message updateClientName(String clientName, Long id) {
        log.info("fixRecharge开始,clientName:{},id:{}", clientName, id);
        testRemoteService.updateClientName(clientName, id);
        log.info("fixRecharge结束,clientName:{},id:{}", clientName, id);
        return Message.success();
    }

    @RequestMapping("/updateVACash")
    public Message updateVACash(BigDecimal cashAmount, BigDecimal freezeAmount, BigDecimal usedAmount, Long id) {
        log.info("fixRecharge开始,cashAmount:{},freezeAmount:{},usedAmount:{},id:{}", cashAmount, freezeAmount, usedAmount, id);
        testRemoteService.updateVACash(cashAmount, freezeAmount, usedAmount, id);
        log.info("fixRecharge,cashAmount:{},freezeAmount:{},usedAmount:{},id:{}", cashAmount, freezeAmount, usedAmount, id);
        return Message.success();
    }

    @RequestMapping("/axoStatu")
    public Message axoStatu(Integer status, Long id) {
        log.info("fixRecharge开始,status:{},id:{}", status, id);
        testRemoteService.updateSaxoStatu(status, id);
        log.info("fixRecharge,status:{},id:{}", status, id);
        return Message.success();
    }

    @RequestMapping("/updateBankVAStatu")
    public Message updateBankVAStatu(Integer status, Long id) {
        log.info("fixRecharge开始,status:{},id:{}", status, id);
        testRemoteService.updateBankVAStatu(status, id);
        log.info("fixRecharge,status:{},id:{}", status, id);
        return Message.success();
    }

    /**
     * 重新计算accountstatic
     * @param accountId
     * @param date
     * @return
     */
    @RequestMapping("/updateAccountStatics")
    public Message updateAccountStatics(Long accountId,Date date){
        testRemoteService.updateAccountStatics(accountId,date);
        return Message.success();
    }

    @RequestMapping("/testErrorHandlingEmail")
    public Message testErrorHandlingEmail(){
        pivotErrorDetailRemoteService.summaryErrorHandlingDetail(DateUtils.now());
        return Message.success();
    }



//    @RequestMapping("/shareDividendJob")
//    public Message shareDividendJob() {
//        log.info("shareDividendJob开始");
//        testRemoteService.updateBankVAStatu();
//        log.info("shareDividendJob结束");
//        return Message.success();
//    }

    @RequestMapping("/dividend")
    public Message dividend(@RequestParam("specialDate")Date specialDate) {
        log.info("dividend开始");
        saxoStatisticRemoteService.dividend(specialDate);
        log.info("dividend结束");
        return Message.success();
    }

    @RequestMapping("/sxaoAccountStatus")
    public Message shareDividendJob(Date date) {
        log.info("sxaoAccountStatusJob开始");
        saxoStatisticRemoteService.totalStatisEnd(date);
        log.info("sxaoAccountStatusJob结束");
        return Message.success();
    }
    @RequestMapping("/recordBookkeepingCash")
    public Message recordBookkeepingCash() {
        log.info("recordBookkeepingCashJob开始");
        saxoStatisticRemoteService.recordBookkeepingCash();
        log.info("recordBookkeepingCash结束");
        return Message.success();
    }

    @RequestMapping("/statisShareTrades")
    public Message statisShareTrades() {
        log.info("statisShareTradesJob开始");
        saxoStatisticRemoteService.statisShareTrades();
        log.info("statisShareTradesJob结束");
        return Message.success();
    }
    @RequestMapping("/statisShareOpenPositions")
    public Message statisShareOpenPositions() {
        log.info("statisShareOpenPositionsJob开始");
        saxoStatisticRemoteService.statisShareOpenPositions();
        log.info("statisShareOpenPositionsJob结束");
        return Message.success();
    }
    @RequestMapping("/recordCashTransactions")
    public Message recordCashTransactions() {
        log.info("recordCashTransactionsJob开始");
        saxoStatisticRemoteService.recordCashTransactions();
        log.info("recordCashTransactionsJob结束");
        return Message.success();
    }

    @RequestMapping("/statisExport")
    public Message statisExport() {
        log.info("statisExport开始");
        saxoStatisticRemoteService.statisExport();
        log.info("statisExport结束");
        return Message.success();
    }

    @RequestMapping("/balanceofaccount")
    public Message balanceofaccount() {
        log.info("balanceofaccount开始");
        saxoStatisticRemoteService.balanceOfAccount();
        log.info("balanceofaccount结束");
        return Message.success();
    }

    @RequestMapping("/userProfit")
    public Message userProfit(Date date) {
        log.info("userProfit开始");
        testRemoteService.userProfit(date);
        log.info("userProfit结束");
        return Message.success();
    }

   /* @RequestMapping("/staticBankVirtualAccount")
    public Message staticBankVirtualAccount(){
        userServiceRemoteService.staticBankVirtualAccountJob();
        return Message.success();
    }*/

    @RequestMapping("/downloadStatement")
    public Message downloadStatement(HttpServletResponse response, HttpServletRequest request){
        String fileName = "statement/52301_custstatement_2019-06.pdf";
        if(StringUtils.isEmpty(fileName)){
            return Message.error("文件名为空");
        }
        try
        {
            S3Object s3Object = AwsUtil.downloadFile(fileName);
            log.info("测试文件下载路径{}",fileName);
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "attachment;fileName=" + setFileDownloadHeader(request, fileName));
            IoUtil.copy(s3Object.getObjectContent(),response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
            return Message.error("下载文件失败"+e.getMessage());
        }
        return Message.success();
    }

    /**
     * 修复月报历史数据
     * @return
     */
    @RequestMapping("/fixCustStatement")
    public Message fixCustStatement(){
        testRemoteService.fixCustStatement();
        userServiceRemoteService.fixBankOrder();
        return Message.success();
    }
}
