package com.pivot.aham.api.server.remoteservice;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.api.server.dto.req.AccountEtfAssetReqDTO;
import com.pivot.aham.api.server.dto.res.AccountEtfAssetResDTO;
import com.pivot.aham.api.server.dto.res.TmpOrderRecordResDTO;
import com.pivot.aham.api.server.dto.res.UserProfitInfoResDTO;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by luyang.li on 18/12/2.
 */
public interface AssetServiceRemoteService extends BaseRemoteService {

    /**
     * 申购Etf完成之后的回调
     *
     * @param params
     * @return
     */
    RpcMessage etfCallBack(List<EtfCallbackDTO> params);

    /**
     * 查询用户资产详情
     *
     * @param userAssetDTO
     * @return
     */
    RpcMessage<UserAssetWapperDTO> queryUserAssets(UserAssetDTO userAssetDTO);

    /**
     * mock Etf申购成功回调分账
     *
     * @param tmpOrderId
     */
    void etfCallBackMock(Long tmpOrderId);

    /**
     * 自建基金净值计算触发
     *
     * @param date
     * @param accountId
     */
    void assetsFundNav(String date, Long accountId);

    /**
     * 查询账户持有的 ETF 的份额
     *
     * @param accountEtfAssetReqDTO
     * @return
     */
    RpcMessage<AccountEtfAssetResDTO> queryAccountEtfShare(AccountEtfAssetReqDTO accountEtfAssetReqDTO);

    /**
     * 查询账户持有的 ETF 的份额
     *
     * @return
     */
    RpcMessage<List<AccountAssetResDTO>> queryAccountAssets(AccountAssetReqDTO accountAssetDTO);

    /**
     * 查询账户持有的 ETF 的份额 (By Date)
     *
     * @param accountAssetDTO
     * @param date
     * @return
     */
    RpcMessage<List<AccountAssetResDTO>> queryAccountAssets(AccountAssetReqDTO accountAssetDTO, Date date);

    /**
     * 查询该portfolio下所有的投资详情
     *
     * @param portfolioId
     */
    RpcMessage<List<UserProfitInfoResDTO>> queryPortfolioReturn(String portfolioId);

//    /**
//     * 用户自建基金净值计算触发
//     */
//    void userAssetsFundNav();
    /**
     * 查询das总现金资产及总持仓金额
     *
     * @return
     */
    RpcMessage<AccountTotalAssetDTO> queryAccountTotalInfo(Date nowDate);

    /**
     * 获取每只ETF的金额，份额
     *
     * @return
     */
    RpcMessage<List<ProductStatisDTO>> querySpecificData(Date nowDate);
    
    RpcMessage<List<TmpOrderRecordResDTO>> getTmpOrderRecord(TmpOrderRecordResDTO tmpOrderRecordResDTO);
    
    void updateTpcfTncf(Long totalTmpOrderId);

    RpcMessage<String> getDasUnit();
    
    RpcMessage<String> saveAhamRecon(List<AccountAssetResDTO> listAhamReconReq);
    
    RpcMessage<JSONArray> findAhamReconPage(Date startCreateTime, Date endCreateTime);
    
    RpcMessage<List<AccountAssetResDTO>> getDasProdUnit();
}
