package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.DividendCallBackDTO;
import com.pivot.aham.api.server.remoteservice.DividendRemoteService;
import com.pivot.aham.api.server.dto.req.UserDividendReqDTO;
import com.pivot.aham.api.server.dto.res.UserDividendResDTO;
import com.pivot.aham.api.service.mapper.model.AccountEtfSharesPO;
import com.pivot.aham.api.service.mapper.model.UserDividendPO;
import com.pivot.aham.api.service.service.AccountAssetService;
import com.pivot.aham.api.service.service.AccountEtfSharesService;
import com.pivot.aham.api.service.service.DividendService;
import com.pivot.aham.api.service.service.UserDividendService;
import com.pivot.aham.common.enums.analysis.DividendHandelTypeEnum;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import com.pivot.aham.common.core.util.DateUtils;
import com.pivot.aham.common.core.util.ErrorLogAndMailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


@Service(interfaceClass = DividendRemoteService.class)
@Slf4j
public class DividendRemoteServiceImpl implements DividendRemoteService {

    @Autowired
    private AccountAssetService accountAssetService;
    @Autowired
    private AccountEtfSharesService accountEtfSharesService;
    @Resource
    private DividendService dividendService;
    @Resource
    private UserDividendService userDividendService;
    @Override
    public RpcMessage<String> dividendCallBack(DividendCallBackDTO dividendCallBackDTO) {
        try {
            log.info("分红回调开始:{}", JSON.toJSONString(dividendCallBackDTO));
            //按exdate-1获取accountnav的总份额
            Date lastExDate = DateUtils.addDateByDay(dividendCallBackDTO.getExDate(), -1);
            AccountEtfSharesPO accountEtfSharesPO = new AccountEtfSharesPO();
            accountEtfSharesPO.setStaticDate(lastExDate);
            accountEtfSharesPO.setProductCode(dividendCallBackDTO.getProductCode());
            List<AccountEtfSharesPO> accountEtfSharesList = accountEtfSharesService.selectByStaticDate(accountEtfSharesPO);
            if (CollectionUtils.isEmpty(accountEtfSharesList)) {
                ErrorLogAndMailUtil.logError(log, "分红回调,没找到除息日ETF信息,除息日");
                throw new BusinessException("分红处理失败:没找到除息日ETF信息");
            }
            //计算总的ETF总份额
            BigDecimal totalShares = BigDecimal.ZERO;
            for (AccountEtfSharesPO accountEtfShares : accountEtfSharesList) {
                totalShares = totalShares.add(accountEtfShares.getShares());
            }
            log.info("进行分红，可以得到分红的,totalShares:{},accounts:{}", totalShares, JSON.toJSONString(accountEtfSharesList));
            //开始处理分红
            dividendService.handelAccountAndUserDividend(accountEtfSharesList, dividendCallBackDTO, totalShares);
            log.info("分红处理结束");
        } catch (Exception ex) {
            log.error("分红处理异常：", ex);
            return RpcMessage.error("分红处理失败");
        }
        return RpcMessage.success(dividendCallBackDTO.getDividendOrderId());
    }

    @Override
    public RpcMessage<BigDecimal> getDividendMoney(Date date) {
        BigDecimal decimal=userDividendService.getListByCond(new UserDividendPO(date, DividendHandelTypeEnum.USED_COMMONWEAL));
        return RpcMessage.success(decimal);

    }
    
   @Override
    public RpcMessage<Page<UserDividendResDTO>> getUserDividendPage(UserDividendReqDTO userDividendReqDTO) {
        Page<UserDividendPO> rowBounds = new Page<>(
                userDividendReqDTO.getPageNo(), userDividendReqDTO.getPageSize());

        UserDividendPO userDividendPO = new UserDividendPO();
        userDividendPO = BeanMapperUtils.map(userDividendReqDTO, UserDividendPO.class);

        Page<UserDividendPO> poPagination = userDividendService.queryPageList(userDividendPO, rowBounds);

        Page<UserDividendResDTO> paginationRes = new Page<>();
        paginationRes = BeanMapperUtils.map(poPagination, paginationRes.getClass());

        List<UserDividendPO> userDividendPOList = poPagination.getRecords();
        List<UserDividendResDTO> userDividendResDTOList = BeanMapperUtils.mapList(userDividendPOList, UserDividendResDTO.class);
        paginationRes.setRecords(userDividendResDTOList);

        return RpcMessage.success(paginationRes);
    }
}
