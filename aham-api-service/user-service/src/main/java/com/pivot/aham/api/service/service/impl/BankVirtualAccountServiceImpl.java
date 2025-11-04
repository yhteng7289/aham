package com.pivot.aham.api.service.service.impl;

import com.pivot.aham.api.server.dto.BankVirtualAccountDTO;
import com.pivot.aham.api.service.mapper.BankVirtualAccountMapper;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccount;
import com.pivot.aham.api.service.mapper.model.BankVirtualAccountOrder;
import com.pivot.aham.api.service.service.BankVirtualAccountOrderService;
import com.pivot.aham.api.service.service.BankVirtualAccountService;
import com.pivot.aham.common.core.base.BaseServiceImpl;
import com.pivot.aham.common.core.support.file.ftp.FTPClientUtil;
import com.pivot.aham.common.core.support.generator.Sequence;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.PropertiesUtil;
import com.pivot.aham.common.enums.CurrencyEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeStatusEnum;
import com.pivot.aham.common.enums.analysis.VAOrderTradeTypeEnum;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by luyang.li on 18/12/3.
 * <p>
 * 虚拟账户
 */

@Service
public class BankVirtualAccountServiceImpl extends BaseServiceImpl<BankVirtualAccount, BankVirtualAccountMapper> implements BankVirtualAccountService {

    @Autowired
    private BankVirtualAccountOrderService bankVirtualAccountOrderService;


    @Override
    public int saveBankVirtualAccount(BankVirtualAccount bankVirtualAccount) {
        return mapper.saveBankVirtualAccount(bankVirtualAccount);
    }

    @Override
    public List<BankVirtualAccount> queryListByVirtualAccounts(List<String> virtualAccountIds) {
        return mapper.queryListByVirtualAccounts(virtualAccountIds);
    }

    @Override
    public int saveBatch(List<BankVirtualAccountDTO> virtualAccountList) {
        List<BankVirtualAccount> bankVirtualAccounts = virtualAccountList.stream().map(item -> {
            BankVirtualAccount virtualAccount = new BankVirtualAccount();
            virtualAccount.setClientId(item.getClientId())
                    .setClientName(item.getClientName())
                    .setVirtualAccountNo(item.getVirtualAccountNo())
                    .setCashAmount(item.getCashAmount())
                    .setUsedAmount(item.getUsedAmount())
                    .setFreezeAmount(item.getFreezeAmount())
                    .setCurrency(item.getCurrency())
                    .setCreateTime(DateUtils.now())
                    .setUpdateTime(DateUtils.now())
                    .setId(Sequence.next());
            return virtualAccount;
        }).collect(Collectors.toList());
        return mapper.saveBatch(bankVirtualAccounts);
    }

    /**
     * 统计账户余额
     *
     * @param bankVirtualAccountParam
     * @return
     */
    @Override
    public void statisticsAmount(BankVirtualAccount bankVirtualAccountParam) {
        BankVirtualAccount bankVirtualAccount = mapper.quaryBankVirtualAccount(bankVirtualAccountParam);
        BankVirtualAccountOrder bankVirtualAccountOrder = new BankVirtualAccountOrder();
        bankVirtualAccountOrder.setVirtualAccountNo(bankVirtualAccountParam.getVirtualAccountNo());
        List<BankVirtualAccountOrder> bankVirtualAccountOrderList = bankVirtualAccountOrderService.listBankVAOrders(bankVirtualAccountOrder);
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

        mapper.updateBankVirtualAccount(bankVirtualAccount);
    }

    @Override
    public List<BankVirtualAccount> queryListByClient(BankVirtualAccount bankVirtualAccount) {
        return mapper.queryListBankVirtualAccount(bankVirtualAccount);
    }

    @Override
    public BankVirtualAccount quaryBankVirtualAccount(BankVirtualAccount queryParam) {
        return mapper.quaryBankVirtualAccount(queryParam);
    }

    @Override
    public List<BankVirtualAccount> queryListBankVirtualAccount(BankVirtualAccount queryParam) {
        return mapper.queryListBankVirtualAccount(queryParam);
    }
    @Override
    public List<BankVirtualAccount> getListByTradeTime(Map<String, Object> params) {
        return mapper.getListByTradeTime(params);
    }
    
         /**
     * Modify by WooiTatt 20190825 Due to export in TXT file. Direct FTP to UOB
     * instead send back to PIVOT
     */
    @Override
    public void executeCreateVirtualAccount(String currency, String numberOfCreation) {
        
        String fileName = PropertiesUtil.getString("uob.sftp.virtualAcc.create")+DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT5) + "01.txt";
        //String ftpPath = UobConstants.getPaymentOrderPath() + "client/" + fileName;
        String fileCreationDate = DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT2);
        String strBankAcc = PropertiesUtil.getString("pivot.account.sgd.number");
               strBankAcc = strBankAcc.replace("-","");
               
         // Set Column Size Header File
         List lsHeaderFormat = new ArrayList();
         lsHeaderFormat.add("%-1s");
         lsHeaderFormat.add("%-10s");
         lsHeaderFormat.add("%-12s");
         lsHeaderFormat.add("%-3s");
         lsHeaderFormat.add("%-34s");
         lsHeaderFormat.add("%-140s");
         lsHeaderFormat.add("%-8s");
         lsHeaderFormat.add("%-792s%n");
         
        //Header Value
         List lsValueHeader = new ArrayList();
         lsValueHeader.add(PropertiesUtil.getString("uob.rec.header.value"));
         lsValueHeader.add(fileName.replace(".txt",""));
         lsValueHeader.add(PropertiesUtil.getString("uob.pivot.company.id"));
         lsValueHeader.add(currency);
         lsValueHeader.add(strBankAcc);
         lsValueHeader.add(PropertiesUtil.getString("pivot.account.name"));
         lsValueHeader.add(fileCreationDate);
         lsValueHeader.add("");
         
         BankVirtualAccount bva = new BankVirtualAccount();
         bva.setCurrency(CurrencyEnum.SGD);
         bva = mapper.queryBankVirtualAccDescByCurrency(bva);
         
        
        System.out.println("fileName >>> " + fileName);
        int seqVirtualNumber = Integer.parseInt(bva.getVirtualAccountNo().substring(4,10));
        System.out.println("Currency >>> " + bva.getCurrency() + " VA >>>" + bva.getVirtualAccountNo() +" ConInteger >>"+ seqVirtualNumber);
        
        // Write Header Line
        int recordCount = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\write\\"+fileName))) {
            // Write Header Line
            for (int i = 0; i < lsHeaderFormat.size(); i++) {
                writer.write(String.format(lsHeaderFormat.get(i).toString(), lsValueHeader.get(i).toString()));
            }
            // Write Body Line
            for(int i=0; i < Integer.valueOf(numberOfCreation); i++){
                seqVirtualNumber +=1;
                writer.write(String.format("%-1s", PropertiesUtil.getString("uob.rec.body.value")));
                writer.write(String.format("%-10s", PropertiesUtil.getString("pivot.account.sgd.biller.code")));
                writer.write(String.format("%-34s", PropertiesUtil.getString("pivot.account.sgd.biller.code")+convertIntegerFormat(String.valueOf(seqVirtualNumber),6)));
                writer.write(String.format("%-6s", ""));
                writer.write(String.format("%-1s", PropertiesUtil.getString("uob.va.mode")));
                writer.write(String.format("%-948s%n", ""));
                recordCount+=1;
            } 
            // Write Traile Line
            writer.write(String.format("%-1s%-8s%-991s", PropertiesUtil.getString("uob.rec.trailer.value"), convertIntegerFormat(String.valueOf(recordCount),8) , ""));
            writer.close();
            
        } catch (IOException e) {
          //  ErrorLogAndMailUtil.logErrorForTrade(log, e);
        } finally {
            // exportExcel.dispose();

        }
    }
    
    public void executeConfirmaVirtualAccount(){
        String fileName =PropertiesUtil.getString("uob.sftp.virtualAcc.confirm")+DateUtils.formatDate(DateUtils.now(), DateUtils.DATE_FORMAT5) + "01O.txt";
        String filePath = "C:\\write\\" + fileName;

        InputStream inputStream = FTPClientUtil.getFtpInputStream(filePath);
        List<String> lines = FTPClientUtil.readFileContent(filePath);

        if (inputStream == null) {
            return;
        }

        try {
            
            List <BankVirtualAccount> lsBVA = new ArrayList();
            
            for (int i = 0; lines.size() - 1 > i; i++) {
                BankVirtualAccount bva = new BankVirtualAccount();
                bva.setVirtualAccountNo(lines.get(i).substring(11, 45));
                bva.setClientName(lines.get(i).substring(92, 232));
                
                lsBVA.add(bva);

            }

            for (BankVirtualAccount bva : lsBVA) {
                try {
                    mapper.saveBankVirtualAccount(bva);
                } catch (Exception e) {
                    //ErrorLogAndMailUtil.logErrorForTrade(log, e);
                }
            }
        } catch (Exception e) {
            //ErrorLogAndMailUtil.logErrorForTrade(log, e);
        }
    }
    
     /**
     * convertIntegerFormat Added by WooiTatt 190825
     */
    private String convertIntegerFormat(String value, int length) {

        BigInteger bAmt = new BigInteger(value);
        String toAmt = String.valueOf(bAmt);

        int amtDigit = length - toAmt.length();
        String format = "";
        for (int i = 0; i < amtDigit; i++) {
            format += "0";
        }

        return format + toAmt;
    }

}
