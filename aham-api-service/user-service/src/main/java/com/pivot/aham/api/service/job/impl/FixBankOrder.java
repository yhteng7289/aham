package com.pivot.aham.api.service.job.impl;

import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountDailyRecord;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.api.service.service.BankVirtualAccountDailyRecordService;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderService;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.enums.analysis.NeedRefundTypeEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class FixBankOrder {
    @Resource
    private BankVirtualAccountDailyRecordService bankVirtualAccountDailyRecordService;
    @Resource
    private BankVirtualAccountService bankVirtualAccountService;
    @Resource
    private BankVirtualAccountOrderService bankVirtualAccountOrderService;

    public void fixBankOrder(){
        Date startTime = DateUtils.parseDate("2019-04-30");
        //结束时间
        Date endTime = DateUtils.dayEnd(new Date());
        for(Date d = startTime;d.compareTo(endTime)<=0;d=DateUtils.addDateByDay(d,1)){
            //获取所以bankaccount
            BankVirtualAccount bankVirtualAccount = new BankVirtualAccount();
            List<BankVirtualAccount> bankVirtualAccountList = bankVirtualAccountService.queryList(bankVirtualAccount);
            //遍历bankaccount
            for(BankVirtualAccount bankVirtual:bankVirtualAccountList) {
                BankVirtualAccountDailyRecord bankVirtualAccountDailyRecordQuery = new BankVirtualAccountDailyRecord();
                bankVirtualAccountDailyRecordQuery.setClientId(bankVirtual.getClientId());
                bankVirtualAccountDailyRecordQuery.setVirtualAccountNo(bankVirtual.getVirtualAccountNo());
                bankVirtualAccountDailyRecordQuery.setStaticDate(d);
                BankVirtualAccountDailyRecord bankVirtualAccountDaily = bankVirtualAccountDailyRecordService.selectByStaticDate(bankVirtualAccountDailyRecordQuery);

                BankVirtualAccount staticedAccount = statisticsAmount(bankVirtual,d);
                staticedAccount.setClientId(bankVirtual.getClientId());
                staticedAccount.setVirtualAccountNo(bankVirtual.getVirtualAccountNo());
                staticedAccount.setCurrency(bankVirtual.getCurrency());


                BankVirtualAccountDailyRecord bankVirtualAccountDailyRecord = new BankVirtualAccountDailyRecord();
                BeanUtils.copyProperties(staticedAccount,bankVirtualAccountDailyRecord);
                bankVirtualAccountDailyRecord.setId(null);
                bankVirtualAccountDailyRecord.setStaticDate(d);
                if(bankVirtualAccountDaily != null) {
                    bankVirtualAccountDailyRecord.setId(bankVirtualAccountDaily.getId());
                }
                bankVirtualAccountDailyRecord.setCreateTime(null);
                bankVirtualAccountDailyRecordService.updateOrInsert(bankVirtualAccountDailyRecord);
            }
        }

    }

    /**
     * 重算资产
     * @param bankVirtualAccountParam
     */
    public BankVirtualAccount statisticsAmount(BankVirtualAccount bankVirtualAccountParam,Date endCreateTime) {
        BankVirtualAccount bankVirtualAccount = new BankVirtualAccount();
        BankVirtualAccountOrder bankVirtualAccountOrder = new BankVirtualAccountOrder();
        bankVirtualAccountOrder.setVirtualAccountNo(bankVirtualAccountParam.getVirtualAccountNo());
        bankVirtualAccountOrder.setEndCreateTime(endCreateTime);
        bankVirtualAccountOrder.setNeedRefundType(NeedRefundTypeEnum.UN_REFUND);
        List<BankVirtualAccountOrder> bankVirtualAccountOrderList = bankVirtualAccountOrderService.listBankVirtualAccountOrders(bankVirtualAccountOrder);
        BigDecimal cashAmount = BigDecimal.ZERO;
        BigDecimal comeInAmount = BigDecimal.ZERO;
        BigDecimal freezeAmount = BigDecimal.ZERO;
        BigDecimal usedAmount = BigDecimal.ZERO;


        for (BankVirtualAccountOrder accountOrder : bankVirtualAccountOrderList) {
            VAOrderTradeTypeEnum operatorType = accountOrder.getOperatorType();

            //支出
            Boolean used = (operatorType == VAOrderTradeTypeEnum.COME_OUT) &&
                    accountOrder.getOrderStatus() == VAOrderTradeStatusEnum.SUCCESS;

            //收入
            Boolean comeIn = (operatorType == VAOrderTradeTypeEnum.COME_INTO) &&
                    accountOrder.getOrderStatus() == VAOrderTradeStatusEnum.SUCCESS;

            //处理中
            Boolean handling = accountOrder.getOrderStatus() == VAOrderTradeStatusEnum.HANDLING;

            if (comeIn) {
                comeInAmount = comeInAmount.add(accountOrder.getCashAmount());
            } else if (used) {
                usedAmount = usedAmount.add(accountOrder.getCashAmount());
            } else if (handling) {
                freezeAmount = freezeAmount.add(accountOrder.getCashAmount());
            }
        }
        //可用金额 = 总收入 - 冻结 - 已使用
        cashAmount = comeInAmount.subtract(freezeAmount).subtract(usedAmount);

        bankVirtualAccount.setCashAmount(cashAmount.setScale(4, BigDecimal.ROUND_HALF_UP));
        bankVirtualAccount.setFreezeAmount(freezeAmount.setScale(4, BigDecimal.ROUND_HALF_UP));
        bankVirtualAccount.setUsedAmount(usedAmount.setScale(4, BigDecimal.ROUND_HALF_UP));
        bankVirtualAccount.setUpdateTime(DateUtils.now());
        return bankVirtualAccount;
    }
}
