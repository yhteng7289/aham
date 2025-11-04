package com.pivot.aham.api.service.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.pivot.aham.api.service.mapper.model.TransOrderPO;
import com.pivot.aham.common.core.base.BaseService;

import java.util.List;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年12月06日
 */
public interface TransOrderService extends BaseService<TransOrderPO> {

    Page<TransOrderPO> queryTransOrder(Page<TransOrderPO> rowBounds, TransOrderPO transOrderPO);

    List<TransOrderPO> queryTransOrderList(TransOrderPO transOrderPO);

}
