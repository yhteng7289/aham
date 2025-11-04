package com.pivot.aham.api.service.remote.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pivot.aham.api.server.dto.SaxoAccountOrderReqDTO;
import com.pivot.aham.api.server.dto.SaxoAccountOrderResDTO;
import com.pivot.aham.api.server.remoteservice.SaxoAccountOrderRemoteService;
import com.pivot.aham.api.service.mapper.model.SaxoAccountOrderPO;
import com.pivot.aham.api.service.service.SaxoAccountOrderService;
import com.pivot.aham.common.core.base.RpcMessage;
import com.pivot.aham.common.core.util.BeanMapperUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;


/**
 * 用户统计信息
 *
 * @author addison
 * @since 2018年12月10日
 */
@Service(interfaceClass = SaxoAccountOrderRemoteService.class)
@Slf4j
public class SaxoAccountOrderRemoteServiceImpl implements SaxoAccountOrderRemoteService {
    @Resource
    private SaxoAccountOrderService saxoAccountOrderService;


    @Override
    public RpcMessage<List<SaxoAccountOrderResDTO>> getSaxoAccountOrders(SaxoAccountOrderReqDTO saxoAccountOrderReqDTO) {

        SaxoAccountOrderPO saxoAccountOrderQuery = new SaxoAccountOrderPO();
        saxoAccountOrderQuery = BeanMapperUtils.map(saxoAccountOrderReqDTO,SaxoAccountOrderPO.class);
        List<SaxoAccountOrderPO> saxoAccountOrderPOList = saxoAccountOrderService.listSaxoAccountOrder(saxoAccountOrderQuery);

        List<SaxoAccountOrderResDTO> saxoAccountOrderResDTOList = BeanMapperUtils.mapList(saxoAccountOrderPOList,SaxoAccountOrderResDTO.class);

        return RpcMessage.success(saxoAccountOrderResDTOList);
    }

    @Override
    public RpcMessage<SaxoAccountOrderResDTO> getSaxoAccountOrder(SaxoAccountOrderReqDTO saxoAccountOrderReqDTO) {
        SaxoAccountOrderPO saxoAccountOrderQuery = new SaxoAccountOrderPO();
        saxoAccountOrderQuery = BeanMapperUtils.map(saxoAccountOrderReqDTO,SaxoAccountOrderPO.class);
        SaxoAccountOrderPO saxoAccountOrderPO = saxoAccountOrderService.selectOne(saxoAccountOrderQuery);

        if(saxoAccountOrderPO == null){
            return RpcMessage.error(saxoAccountOrderReqDTO.getId()+"未找到该saxo订单");
        }

        SaxoAccountOrderResDTO saxoAccountOrderResDTOList = BeanMapperUtils.map(saxoAccountOrderPO,SaxoAccountOrderResDTO.class);

        return RpcMessage.success(saxoAccountOrderResDTOList);
    }
}
