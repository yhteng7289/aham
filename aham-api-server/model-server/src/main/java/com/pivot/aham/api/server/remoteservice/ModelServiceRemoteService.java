package com.pivot.aham.api.server.remoteservice;

import com.pivot.aham.api.server.dto.*;
import com.pivot.aham.common.core.base.BaseRemoteService;
import com.pivot.aham.common.core.base.RpcMessage;

import java.text.ParseException;
import java.util.List;

/**
 * Created by luyang.li on 18/12/2.
 */
public interface ModelServiceRemoteService extends BaseRemoteService {

    /**
     * 查询所有有效的模型
     *
     * @return
     */
    List<ModelRecommendResDTO> getAllValidRecommend();

    /**
     * 根据portfolioId查询有效的模型
     *
     * @return
     */
    ModelRecommendResDTO getValidRecommendByPortfolioId(String portfolioId);

    /**
     * 根据日期获取模型
     *
     * @param modelRecommendDTO
     * @return
     */
    List<ModelRecommendResDTO> getModelRecommendByDate(ModelRecommendDTO modelRecommendDTO);

    /**
     * 根据id获取
     *
     * @param modelId
     * @return
     */
    ModelRecommendResDTO getModelRecommendById(Long modeRecommendlId);

    /**
     * 查询收益曲线
     *
     * @return
     * @param portLevelDTO
     */
    List<PortLevelResDTO> getPortLevel(PortLevelDTO portLevelDTO);

    ModelRecommendResDTO queryValidModelByPortfolioId(ModelRecommendDTO modelRecommendDTO);

    /**
     * 查询所有基金信息
     *
     * @return
     */
    List<ProductInfoResDTO> queryAllProductInfo();

    /**
     * 同步模型数据任务触发
     *
     * @param date
     */
    void triggerRecommendJob(String date);

    /**
     * 同步收益文件
     */
    void triggerPortlevel();

    /**
     * 根据portfolio查询模型数据
     *
     * @param portfolioId
     * @return
     */
    RecommendPortfolioResDTO queryByPortfolio(String portfolioId) throws ParseException;

    /**
     * 同步历史模型数据
     *
     * @param date
     */
    void triggerHisRecommendJob(String date, int days);

    /**
     * 预计收益曲线
     *
     * @return
     * @param dto
     */
    RpcMessage<PortFutureLevelResWrapper> queryPortFutureLevel(PortFutureLevelDTO dto);

    /**
     * 初始化收益对标曲线
     */
    void portLevelInit();

    /**
     * 初始化模型数据
     *
     * @param date
     */
    void modelRecommendInit(String date);

    /**
     * 初始化预计收益
     */
    void portFutureLevelInit();

    /**
     * 查询模型详情 (FE 每日同步一次模型数据)
     *
     * @param modelRecommendDTO
     * @return
     */
    RpcMessage<List<ModelRecommendResWrapper>> getModelRecommendDetail(ModelRecommendDTO modelRecommendDTO);

    /**
     * 根据portfolioId查询模型详情 (APP)
     *
     * @param modelRecommendForAppDTO
     * @return
     */
    RpcMessage<ModelRecommendForAppResWrapper> getModelRecommendDetailForApp(ModelRecommendForAppDTO modelRecommendForAppDTO);

    List<PortLevelResDTO> getPortLevelForApp(PortLevelDTO portLevelDTO);

    void triggerAhamRecommendJob();
}
